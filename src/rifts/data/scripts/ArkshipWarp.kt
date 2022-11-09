package rifts.data.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.JumpPointAPI
import com.fs.starfarer.api.campaign.PlanetAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.util.Misc
import lunalib.util.LunaMisc
import org.lazywizard.lazylib.MathUtils
import rifts.data.util.RiftData
import rifts.data.util.WormholeGenerator

class ArkshipWarp(oldArkship: SectorEntityToken, isDestinationPlayer: Boolean) : EveryFrameScript
{

    private var timerID: String = "ArkshipRiftVisualTimer_${MathUtils.getRandomNumberInRange(0, 50000)}"

    private var oldArkship: SectorEntityToken
    private var oldArkshipMarket: MarketAPI


    private var isDestinationPlayer: Boolean
    private var wormholes: List<SectorEntityToken>
    private var destination: StarSystemAPI


    private var wormholeAlpha: Int = 0
    private var wormholeAlphaFloat: Float = 0f

    private var wormholeSpeedMod = 1f
    private var wormholeSizeMod = 1f

    private var isDone = false
    private var triggeredArkshipMove = false
    private var triggeredHyperjump = false

    init {
        this.isDestinationPlayer = isDestinationPlayer
        this.oldArkship = oldArkship

        this.oldArkshipMarket = oldArkship.market

        //Generates a random destination
        if (!isDestinationPlayer)
        {
            destination = findLocationForWormhole()
        }
        else
        {
            destination = Global.getSector().playerFleet.starSystem
        }

        wormholes = WormholeGenerator.createTwoWayWormhole(oldArkship, destination.center, LunaMisc.randomColor(255))

        //Logic for the first scripted warp
        if (!isDestinationPlayer)
        {
            wormholes[0].setCircularOrbit(oldArkship.orbitFocus, oldArkship.circularOrbitAngle,oldArkship.circularOrbitRadius, oldArkship.circularOrbitPeriod)
            wormholes[1].setCircularOrbit(destination.center, MathUtils.getRandomNumberInRange(0f, 360f), MathUtils.getRandomNumberInRange(5000f, 7000f), 500f)
        }

        //Logic for normal Warps
        else
        {

            var planet = locatePlanet()
            var orbitDistance = 500f
            if (planet!!.isStar)
            {
                orbitDistance = MathUtils.getRandomNumberInRange(4000f, 5000f)
            }
            if (planet!!.isGasGiant)
            {
                orbitDistance = MathUtils.getRandomNumberInRange(800f, 1000f)
            }

            wormholes[0].setCircularOrbit(oldArkship.orbitFocus, oldArkship.circularOrbitAngle,oldArkship.circularOrbitRadius, oldArkship.circularOrbitPeriod)
            wormholes[1].setCircularOrbit(planet, MathUtils.getRandomNumberInRange(0f, 360f), orbitDistance, 300f)
        }

        if (!isDestinationPlayer)
        {
            wormholes[0].addTag(Tags.NON_CLICKABLE)
            wormholes[1].addTag(Tags.NON_CLICKABLE)
        }
        oldArkship.addTag(Tags.NON_CLICKABLE)

        LunaMisc.addCampaignTimer(timerID)
    }


    override fun advance(amount: Float)
    {
        if (!triggeredArkshipMove)
        {
            triggeredArkshipMove = true


            if (!isDestinationPlayer)
            {
                Global.getSector().playerFleet.stats.fleetwideMaxBurnMod.modifyFlat("ArkshipWarp", -20f)
            }
        }

        if (LunaMisc.getCampaignTimer(timerID) in 0f..1f)
        {
            wormholeAlphaFloat += 1f
            wormholeAlpha = wormholeAlphaFloat.toInt()
            wormholeSizeMod += 0.004f
            wormholeSpeedMod += 0.075f

            wormholeAlpha = MathUtils.clamp(wormholeAlpha, 0, 200)
        }
        else if (LunaMisc.getCampaignTimer(timerID) in 1f..2f)
        {
            if (!triggeredHyperjump)
            {

                val oldSystem: StarSystemAPI = oldArkship.getStarSystem()
                var name = oldArkship.name
                oldArkship.forceSensorFaderBrightness(Math.min(oldArkship.getSensorFaderBrightness(), 0f))
                oldArkship.setAlwaysUseSensorFaderBrightness(true)
                oldArkship.setExpired(true)
                oldSystem.removeEntity(oldArkship)

                Global.getSector().economy.removeMarket(oldArkship.market)


                val NewArkship: SectorEntityToken = wormholes[1].starSystem.addCustomEntity("Arkship", name, "Arkship_Entity", "independent")
                for (tag in oldArkship.tags)
                {
                    NewArkship.addTag(tag)
                }

                if (!isDestinationPlayer)
                {
                    Global.getSector().playerFleet.stats.fleetwideMaxBurnMod.modifyFlat("ArkshipWarp", -20f)
                    NewArkship.setCircularOrbitPointingDown(wormholes[1], MathUtils.getRandomNumberInRange(0f, 360f), 300f, 310f)
                    Global.getSector().doHyperspaceTransition(Global.getSector().playerFleet, wormholes[0], JumpPointAPI.JumpDestination(wormholes[1], ""), 0f)
                    Global.getSector().playerFleet.stats.fleetwideMaxBurnMod.unmodify("ArkshipWarp")
                    NewArkship.customDescriptionId = "arkship_after_warp"
                }
                else
                {
                    NewArkship.customDescriptionId = "arkship"
                    NewArkship.setCircularOrbitPointingDown(wormholes[1].orbitFocus, wormholes[1].circularOrbitAngle, wormholes[1].circularOrbitRadius, wormholes[1].circularOrbitPeriod)
                }
                NewArkship.removeTag(Tags.NON_CLICKABLE)
                NewArkship.market = oldArkshipMarket

                triggeredHyperjump = true

            }

            if (isDestinationPlayer)
            {
                wormholeAlphaFloat -= 0.8f
                wormholeAlpha = wormholeAlphaFloat.toInt()
                wormholeSizeMod -= 0.006f
                wormholeSpeedMod -= 0.050f
            }
            else
            {
                wormholeSizeMod -= 0.004f
                wormholeSpeedMod -= 0.075f
            }
        }
        else if (LunaMisc.getCampaignTimer(timerID) >= 2f)
        {
            if (isDestinationPlayer)
            {
                wormholes[0].forceSensorFaderBrightness(Math.min(wormholes[0].getSensorFaderBrightness(), 0f))
                wormholes[0].setAlwaysUseSensorFaderBrightness(true)
                wormholes[0].setExpired(true)
                wormholes[0].starSystem.removeEntity(wormholes[0])

                wormholes[1].forceSensorFaderBrightness(Math.min(wormholes[1].getSensorFaderBrightness(), 0f))
                wormholes[1].setAlwaysUseSensorFaderBrightness(true)
                wormholes[1].setExpired(true)
                wormholes[1].starSystem.removeEntity(wormholes[1])
            }
            if (!isDestinationPlayer)
            {
                wormholes[0].removeTag(Tags.NON_CLICKABLE)
                wormholes[1].removeTag(Tags.NON_CLICKABLE)
            }

            isDone = true

            LunaMisc.removeCampaignTimer(timerID)
        }

        var min = 1f
        if (isDestinationPlayer) min = 0f


        wormholeSizeMod = MathUtils.clamp(wormholeSizeMod, min, 10f)
        wormholeSpeedMod = MathUtils.clamp(wormholeSpeedMod, min, 100f)
        wormholeAlpha = MathUtils.clamp(wormholeAlpha, 0, 200)

        wormholes[0].memoryWithoutUpdate.set("\$WormholeAlpha", wormholeAlpha)
        wormholes[0].memoryWithoutUpdate.set("\$WormholeSpeedMult", wormholeSpeedMod)
        wormholes[0].memoryWithoutUpdate.set("\$WormholeSizeMult", wormholeSizeMod)

        wormholes[1].memoryWithoutUpdate.set("\$WormholeAlpha", wormholeAlpha)
        wormholes[1].memoryWithoutUpdate.set("\$WormholeSpeedMult", wormholeSpeedMod)
        wormholes[1].memoryWithoutUpdate.set("\$WormholeSizeMult", wormholeSizeMod)

    }

    override fun isDone(): Boolean {
        return isDone
    }

    override fun runWhilePaused(): Boolean {
        return false
    }

    private fun findLocationForWormhole() : StarSystemAPI
    {
        var systems: MutableList<StarSystemAPI> = Global.getSector().starSystems
        var filteredSystems: MutableList<StarSystemAPI> = ArrayList()

        for (system in systems)
        {
            if (system.hasTag(Tags.THEME_UNSAFE)) continue
            if (system.hasTag(Tags.THEME_CORE_POPULATED)) continue
            if (system.hasTag(Tags.SYSTEM_CUT_OFF_FROM_HYPER)) continue
            if (system.hasTag(RiftData.DimensionalRift)) continue

            if (system.planets.size >= 1)
            {
                filteredSystems.add(system)
            }
        }
        var system: StarSystemAPI = filteredSystems.get(MathUtils.getRandomNumberInRange(0, filteredSystems.size - 1))

        return system
    }

    private fun locatePlanet(): PlanetAPI? {
        val nearbyPlanets: List<PlanetAPI> = Global.getSector().playerFleet.starSystem.planets
        var shortestDistance = 100000000f
        var targetPlanet: PlanetAPI? = null

        if (nearbyPlanets.size <= 1)
        {
            targetPlanet = nearbyPlanets.get(0)
            return targetPlanet
        }

        for (planet in nearbyPlanets) {
            val distance = Misc.getDistance(Global.getSector().playerFleet, planet)
            if (distance <= shortestDistance) {
                shortestDistance = distance
                targetPlanet = planet
            }
        }
        return targetPlanet
    }

}