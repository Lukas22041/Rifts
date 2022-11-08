package rifts.data.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes
import com.fs.starfarer.api.impl.campaign.ids.Tags
import lunalib.Util.LunaMisc
import org.lazywizard.lazylib.MathUtils
import rifts.data.campaign.procgen.RiftsGenerator
import rifts.data.util.ChiralitySpawner
import rifts.data.util.RiftStrings
import rifts.data.util.WormholeGenerator
import java.awt.Color
import java.util.*


class OriginEvent(star: PlanetAPI) : EveryFrameScript {

    var timerID = "OriginEventTimer"
    var done = false

    var startedTimer = false
    var started = false
    var spawnedFleet = false

    private var wormholeAlpha: Int = 0
    private var wormholeAlphaFloat: Float = 0f

    private var wormholeSpeedMod = 2f
    private var wormholeSizeMod = 0.1f
    private var wormholes: List<SectorEntityToken> = ArrayList()

    var star: PlanetAPI

    init {
        this.star = star
    }

    override fun advance(amount: Float)
    {
        star.addTag(Tags.NON_CLICKABLE)
        var system = star.starSystem
        var ui = Global.getSector().campaignUI

        if (Global.getSector().playerFleet.starSystem == star.starSystem && !startedTimer)
        {
            LunaMisc.addCampaignTimer(timerID)
            startedTimer = true
        }
        else
        {
            var timer: Float = LunaMisc.getCampaignTimer(timerID)
            if (timer >= 0.5f && !started)
            {
                RiftsGenerator.spawnAllRifts()
                var arkship = Global.getSector().getEntityById("Arkship")
                wormholes = WormholeGenerator.createTwoWayWormhole(arkship.starSystem.center, star, Color(0,255,170,255))
                Global.getSoundPlayer().playSound("terrain_hyperspace_lightning", 1f, 1f, wormholes[1].location, wormholes[1].velocity);

                wormholes[0].memoryWithoutUpdate.set("\$WormholeColor", Color(180,21,75,255))
                wormholes[0].setCircularOrbitPointingDown(arkship.starSystem.center, 0f, 410f, 100f)
                wormholes[1].setCircularOrbitPointingDown(star, 0f, 700f, 100f)
                wormholes[0].addTag(Tags.NON_CLICKABLE)
                wormholes[1].addTag(Tags.NON_CLICKABLE)
                star.starSystem.addTag(RiftStrings.hasWormhole)

                wormholeSizeMod += 0f
                wormholeSpeedMod += 0f

                started = true
            }
            if (timer in 0.6f..2f)
            {
                wormholeAlphaFloat += 2f
                wormholeAlpha = wormholeAlphaFloat.toInt()
                wormholeSizeMod += 0.003f
                wormholeSpeedMod += 0.04f

                wormholeAlpha = MathUtils.clamp(wormholeAlpha, 0, 200)
                wormholeSizeMod = MathUtils.clamp(wormholeSizeMod, 0f, 1f)
                wormholeSpeedMod = MathUtils.clamp(wormholeSpeedMod, 1f, 10f)
            }
            if (timer > 1.5f && !spawnedFleet)
            {
                var fleet = ChiralitySpawner.spawnChiralFleet(wormholes[1], FleetTypes.PATROL_MEDIUM, 75f)
                wormholes[0].removeTag(Tags.NON_CLICKABLE)
                wormholes[1].removeTag(Tags.NON_CLICKABLE)
                spawnedFleet = true
            }
            if (timer in 2f..2.5f)
            {
                wormholeSpeedMod -= 0.04f
                wormholeSpeedMod = MathUtils.clamp(wormholeSpeedMod, 1f, 10f)
            }
            if (timer > 2.5f)
            {

                LunaMisc.removeCampaignTimer(timerID)
                done = true
            }

            if (wormholes.isNotEmpty())
            {
                wormholes[0].memoryWithoutUpdate.set("\$WormholeAlpha", wormholeAlpha)
                wormholes[0].memoryWithoutUpdate.set("\$WormholeSpeedMult", wormholeSpeedMod)
                wormholes[0].memoryWithoutUpdate.set("\$WormholeSizeMult", wormholeSizeMod)

                wormholes[1].memoryWithoutUpdate.set("\$WormholeAlpha", wormholeAlpha)
                wormholes[1].memoryWithoutUpdate.set("\$WormholeSpeedMult", wormholeSpeedMod)
                wormholes[1].memoryWithoutUpdate.set("\$WormholeSizeMult", wormholeSizeMod)
            }
        }
    }

    override fun isDone(): Boolean {
        return done
    }

    override fun runWhilePaused(): Boolean {
        return false
    }
}