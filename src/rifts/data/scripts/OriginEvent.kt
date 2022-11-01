package rifts.data.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.PlanetAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.impl.campaign.ids.Tags
import lunalib.Util.LunaMisc
import org.lazywizard.lazylib.MathUtils
import rifts.data.campaign.procgen.RiftsGenerator
import rifts.data.campaign.interaction.OriginInteraction
import rifts.data.util.RiftStrings
import rifts.data.util.WormholeGenerator
import java.awt.Color


class OriginEvent(star: PlanetAPI) : EveryFrameScript {

    var timerID = "OriginEventTimer"
    var done = false

    var startedTimer = false
    var firstDialogue = false
    var spawnedWormhole = false

    var corona: SectorEntityToken

    var star: PlanetAPI

    init {
        this.star = star
        corona = star.starSystem.addCorona(star, 10f, 0f, 0f, 5f)
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
        else if (!spawnedWormhole)
        {
            var timer: Float = LunaMisc.getCampaignTimer(timerID)
            if (timer >= 0.5f && !firstDialogue)
            {
                ui.showInteractionDialog(OriginInteraction("start"), Global.getSector().playerFleet)
                firstDialogue = true

                star.starSystem.removeEntity(star)
                star = system.initStar("Origin2", "origin_star_no_icon", 100f, 0f)
                star.addTag(Tags.NON_CLICKABLE)
            }

            if (timer in 0.5f..3f)
            {
                star.radius += (timer - 0.5f) * 1.5f
                star.starSystem.removeEntity(corona)
                corona = star.starSystem.addCorona(star, star.radius / 2, 0f, 0f, 5f)
            }
            if (timer in 3.1f..3.3f)
            {
                var radius = star.radius + 100f
                radius = MathUtils.clamp(radius, 50f, 10000f)
                star.radius = radius
                star.starSystem.removeEntity(corona)
                corona = star.starSystem.addCorona(star, star.radius / 2, 0f, 0f, 5f)
            }
            if (timer > 3.3f && !spawnedWormhole)
            {
                spawnedWormhole = true

                star.starSystem.removeEntity(star)
                star = system.initStar("Origin2", "origin_star", 100f, 0f)
                star.addTag(Tags.NON_CLICKABLE)

                star.radius = 50f
                star.starSystem.removeEntity(corona)
                corona = star.starSystem.addCorona(star, star.radius / 2, 0f, 0f, 5f)

                RiftsGenerator.spawnAllRifts()
                var arkship = Global.getSector().getEntityById("Arkship")
                var wormholes = WormholeGenerator.createTwoWayWormhole(arkship.starSystem.center, star, Color(0,255,170,255))

                wormholes[0].setCircularOrbitPointingDown(arkship.starSystem.center, 0f, 110f, 100f)
                wormholes[1].setCircularOrbitPointingDown(star, 0f, 500f, 100f)
                star.starSystem.addTag(RiftStrings.hasWormhole)

                var jumppoint = Global.getFactory().createJumpPoint("originJumpoint", "Hyperspace Jumpoint")
                jumppoint.setCircularOrbitPointingDown(star, 180f, 500f, 100f)
                star.starSystem.addEntity(jumppoint)

                star.starSystem.autogenerateHyperspaceJumpPoints(false, false)

                LunaMisc.removeCampaignTimer(timerID)
                done = true
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