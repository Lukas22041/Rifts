package rifts.data.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.impl.campaign.ids.Tags
import lunalib.util.LunaMisc
import org.lazywizard.lazylib.MathUtils
import rifts.data.util.WormholeGenerator

class OriginWormhole(system: StarSystemAPI, crate: SectorEntityToken) : EveryFrameScript
{

    var remove = false
    var timerID = "OriginWormholeTimerID"

    var originSystem: StarSystemAPI
    var crate: SectorEntityToken
    var chiralitySystem: StarSystemAPI? = null

    private var wormholeAlpha: Int = 0
    private var wormholeAlphaFloat: Float = 0f

    private var wormholeSpeedMod = 0f
    private var wormholeSizeMod = 0f


    var wormholes: MutableList<SectorEntityToken>
    init {

        originSystem = system
        this.crate = crate

        var systems = Global.getSector().starSystems

        for (target in systems)
        {
            if (target.name == "Chirality System")
            {
                chiralitySystem = target
                break
            }
        }

        crate.addTag(Tags.NON_CLICKABLE)
        wormholes = WormholeGenerator.createTwoWayWormhole(crate, chiralitySystem!!.star, LunaMisc.randomColor(255))
        wormholes[0].addTag(Tags.NON_CLICKABLE)
        wormholes[1].addTag(Tags.NON_CLICKABLE)

        wormholes[0].location.set(crate.location)
        originSystem.removeEntity(crate)

        wormholes[1].setCircularOrbit(chiralitySystem!!.star, MathUtils.getRandomNumberInRange(0f, 360f), 3000f, 300f)

        LunaMisc.addCampaignTimer(timerID)
    }

    override fun isDone(): Boolean {
        return remove
    }

    override fun runWhilePaused(): Boolean {
       return false
    }

    override fun advance(amount: Float)
    {

        if (LunaMisc.getCampaignTimer(timerID) in 0f..1f)
        {
            wormholeAlphaFloat += 1f
            wormholeAlpha = wormholeAlphaFloat.toInt()
            wormholeSizeMod += 0.003f
            wormholeSpeedMod += 0.055f

            wormholeAlpha = MathUtils.clamp(wormholeAlpha, 0, 200)
        }
        else
        {
            wormholes[0].removeTag(Tags.NON_CLICKABLE)
            wormholes[1].removeTag(Tags.NON_CLICKABLE)
            LunaMisc.removeCampaignTimer(timerID)
            remove = true
        }

        wormholes[0].memoryWithoutUpdate.set("\$WormholeAlpha", wormholeAlpha)
        wormholes[0].memoryWithoutUpdate.set("\$WormholeSpeedMult", wormholeSpeedMod)
        wormholes[0].memoryWithoutUpdate.set("\$WormholeSizeMult", wormholeSizeMod)

        wormholes[1].memoryWithoutUpdate.set("\$WormholeAlpha", wormholeAlpha)
        wormholes[1].memoryWithoutUpdate.set("\$WormholeSpeedMult", wormholeSpeedMod)
        wormholes[1].memoryWithoutUpdate.set("\$WormholeSizeMult", wormholeSizeMod)


    }
}