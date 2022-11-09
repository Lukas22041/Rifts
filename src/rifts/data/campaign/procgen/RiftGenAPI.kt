package rifts.data.campaign.procgen

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.PlanetAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.fleet.FleetMemberType
import com.fs.starfarer.api.impl.MusicPlayerPluginImpl
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3
import com.fs.starfarer.api.impl.campaign.ids.Abilities
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes
import com.fs.starfarer.api.impl.campaign.ids.MemFlags
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec
import com.fs.starfarer.api.util.WeightedRandomPicker
import lunalib.util.LunaMisc
import org.apache.log4j.Level
import org.lazywizard.lazylib.MathUtils
import rifts.data.campaign.procgen.specs.RiftSpec
import rifts.data.campaign.procgen.specs.StarTypeSpec
import rifts.data.scripts.ChiralityStationFleetManager
import rifts.data.util.*
import java.awt.Color


abstract class RiftGenAPI()
{

    private val log = Global.getLogger(RiftGenAPI::class.java)
    init {
        log.level = Level.ALL
    }

    abstract fun generate(riftSpec: RiftSpec, starSpec: StarTypeSpec)

    final fun initiateRift(riftSpec: RiftSpec, starSpec: StarTypeSpec, starRadius: Float, starCorona: Float) : StarSystemAPI
    {
        //Generate Star
        var rift: StarSystemAPI = Global.getSector().createStarSystem("Dimensional Rift")

        var starPicker: WeightedRandomPicker<String> = WeightedRandomPicker()
        starPicker.addAll(starSpec.Stars)
        var backgroundPicker: WeightedRandomPicker<String> = WeightedRandomPicker()
        backgroundPicker.addAll(riftSpec.RiftBackgrounds)
        var star: String = starPicker.pick()

        if (star == "none")
        {
            rift.initNonStarCenter()
            rift.lightColor = Color(170, 170, 170, 255)
        }
        else
        {
            var star: PlanetAPI = rift.initStar("Rift Star", star, starRadius, starCorona)
            rift.lightColor = star.spec.atmosphereColor
        }

        rift.addTag(Tags.SYSTEM_CUT_OFF_FROM_HYPER)
        rift.addTag(RiftData.DimensionalRift)
        rift.addTag(Tags.THEME_HIDDEN)

        rift.backgroundTextureFilename = "graphics/backgrounds/${backgroundPicker.pick()}";
        rift.memoryWithoutUpdate.set(MusicPlayerPluginImpl.MUSIC_SET_MEM_KEY, "rifts_overworld")
        rift.memoryWithoutUpdate.set(RiftData.riftSpecMemoryKey, riftSpec)

        //rift.location.set(100000f, 100000f)

        return rift;
    }

    final fun generateRiftWormholes(rift: SectorEntityToken, riftOrbitRadius: Float, color: Color) : MutableList<SectorEntityToken>
    {
        return generateRiftWormholes(rift, MathUtils.getRandomNumberInRange(0f, 360f), riftOrbitRadius, MathUtils.getRandomNumberInRange(100f,200f), color)
    }

    final fun generateRiftWormholes(rift: SectorEntityToken, riftOrbitAngle: Float, riftOrbitRadius: Float, riftOrbitDays: Float, color: Color) : MutableList<SectorEntityToken>
    {
        var planet = findLocationForWormhole()
        var wormholes = WormholeGenerator.createTwoWayWormhole(rift, planet, color)

        var destinationOrbitRadius = 5000f

        if (planet.isStar || planet.isSystemCenter)
        {
            destinationOrbitRadius = MathUtils.getRandomNumberInRange(4000f, 6000f)
        }
        else
        {
            if (planet.isGasGiant)
            {
                destinationOrbitRadius = MathUtils.getRandomNumberInRange(700f, 800f)
            }
            else
            {
                destinationOrbitRadius = MathUtils.getRandomNumberInRange(550f, 600f)
            }
        }

        wormholes.get(0).setCircularOrbit(rift, riftOrbitAngle, riftOrbitRadius, riftOrbitDays)
        wormholes.get(1).setCircularOrbit(planet, MathUtils.getRandomNumberInRange(0f,360f), destinationOrbitRadius, 200f)

        planet.starSystem.addTag(RiftData.hasWormhole)
        rift.starSystem.location.set(planet.starSystem.location)

        return wormholes
    }

    private fun findLocationForWormhole() : PlanetAPI
    {
        var systems: MutableList<StarSystemAPI> = Global.getSector().starSystems
        var filteredSystems: MutableList<StarSystemAPI> = ArrayList()

        for (system in systems)
        {
            if (system.hasTag(Tags.THEME_CORE_POPULATED)) continue
            if (system.hasTag(Tags.SYSTEM_CUT_OFF_FROM_HYPER)) continue
            if (system.hasTag(RiftData.DimensionalRift)) continue

            if (system.planets.size >= 1 && !system.hasTag(RiftData.DimensionalRift))
            {
                filteredSystems.add(system)
            }
        }

        var system: StarSystemAPI = filteredSystems.get(MathUtils.getRandomNumberInRange(0, filteredSystems.size - 1))
        var planets: List<PlanetAPI> = system.planets

        return planets.get(MathUtils.getRandomNumberInRange(0, planets.size - 1))
    }

    final fun generateUnknownStation(targetOrbit: SectorEntityToken, orbitRadius: Float, minFleets: Int, maxFleets: Int, minPoints: Int, maxPoints: Int): CampaignFleetAPI
    {
        val fleet = FleetFactoryV3.createEmptyFleet("chirality", FleetTypes.BATTLESTATION, null)
        val member: FleetMemberAPI = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "remnant_station2_Standard")
        fleet.fleetData.addFleetMember(member)

        fleet.memoryWithoutUpdate[MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE] = true
        fleet.memoryWithoutUpdate[MemFlags.MEMORY_KEY_NO_JUMP] = true
        fleet.memoryWithoutUpdate[MemFlags.MEMORY_KEY_MAKE_ALLOW_DISENGAGE] = true
        fleet.addTag(Tags.NEUTRINO_HIGH)
        fleet.isStationMode = true

        targetOrbit.starSystem.addEntity(fleet)

        fleet.clearAbilities()
        fleet.addAbility(Abilities.TRANSPONDER)
        fleet.getAbility(Abilities.TRANSPONDER).activate()
        fleet.detectedRangeMod.modifyFlat("gen", 1000f)

        fleet.ai = null

        member.repairTracker.cr = member.repairTracker.maxCR
        fleet.setCircularOrbit(targetOrbit, MathUtils.getRandomNumberInRange(0f,360f), orbitRadius, 100f)

        val activeFleets = ChiralityStationFleetManager(fleet,
            10f,
            minFleets,
            maxFleets,
            25f,
            minPoints,
            maxPoints)
        targetOrbit.starSystem.addScript(activeFleets)

        return fleet
    }

    final fun generateRuins(rift: StarSystemAPI, minRuins: Int, maxRuins: Int)
    {
        var ruinsAmount = MathUtils.getRandomNumberInRange(minRuins, maxRuins)
        var planets = rift.planets.toMutableList()
        planets.shuffle()

        for (planet in planets)
        {
            if (ruinsAmount > 0 && !planet.isStar)
            {
                planet.addTag(RiftRuinsData.commonRuinsTag)
                ruinsAmount--

                var loot = RuinsLoot(MathUtils.getRandomNumberInRange(0f, 40f), MathUtils.getRandomNumberInRange(20f, 70f), MathUtils.getRandomNumberInRange(0f, 5f))

                planet.memoryWithoutUpdate.set(RiftRuinsData.salvageDataMemory, loot)
                if (LunaMisc.randomBool(60))
                {
                    val params = FleetParamsV3(null,
                        null,
                        "chirality",
                        5f,
                        FleetTypes.PATROL_MEDIUM,
                        MathUtils.getRandomNumberInRange(70f, 140f),  // combatPts
                        0f,  // freighterPts
                        0f,  // tankerPts
                        0f,  // transportPts
                        0f,  // linerPts
                        0f,  // utilityPts
                        0f // qualityMod
                    )
                    var defender = FleetFactoryV3.createFleet(params)
                    planet.memoryWithoutUpdate.set("\$hasDefenders", true)
                    planet.memoryWithoutUpdate.set("\$defenderFleet", defender)

                    val data = SalvageEntityGenDataSpec.DropData()

                    var chances = 0
                    for (member in defender.fleetData.membersListCopy) {
                        chances =
                            if (member.isCapital) +16 else if (member.isCruiser) +12 else if (member.isDestroyer) +8 else if (member.isFrigate) +4 else +2
                    }
                    data.group = "rifts_drop"
                    data.chances = chances * 5
                    defender.addDropRandom(data)

                }
            }
        }
    }
}