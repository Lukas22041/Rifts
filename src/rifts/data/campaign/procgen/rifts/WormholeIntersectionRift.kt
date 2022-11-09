package rifts.data.campaign.procgen.rifts

import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.impl.campaign.ids.Terrain
import lunalib.util.LunaMisc
import lunalib.util.LunaProcGen
import org.lazywizard.lazylib.MathUtils
import rifts.data.campaign.procgen.RiftGenAPI
import rifts.data.campaign.procgen.specs.RiftSpec
import rifts.data.campaign.procgen.specs.StarTypeSpec
import rifts.data.util.RiftRuinsData
import rifts.data.util.RiftData
import java.awt.Color


class WormholeIntersectionRift : RiftGenAPI()
{
    override fun generate(riftSpec: RiftSpec, starSpec: StarTypeSpec)
    {
        var rift: StarSystemAPI = initiateRift(riftSpec, starSpec, 100f, 0f)

        rift.star.addTag("CanCreateWormhole")

        //Generate Rifts
        var wormhole1 = generateRiftWormholes(rift.center, 0f, 700f, 200f, LunaMisc.randomColor(255))
        var wormhole2 = generateRiftWormholes(rift.center, 180f, 700f, 200f, LunaMisc.randomColor(255))

        var orbitDistance = MathUtils.getRandomNumberInRange(2500f, 3000f)

        //Asteroids at Rifts
        rift.addAsteroidBelt(rift.center, 10, 900f, 256f, 100f, 100f, Terrain.ASTEROID_BELT, "");
        rift.addRingBand(rift.center, "misc", "rings_asteroids0", 256f, 0, Color.gray, 256f, 900f, 100f);

        //Generate only required planet and Unknown Station
        var planet1 = LunaProcGen.generatePlanet(rift, "OriginStarPlanet", "Planet", orbitDistance);
        planet1.addTag(RiftData.RiftPlanet)
        planet1.addTag(RiftRuinsData.cluePlanet2Tag)

        //Asteroids
        rift.addAsteroidBelt(rift.center, 100, orbitDistance + 350, 256f, 100f, 100f, Terrain.ASTEROID_BELT, "");
        rift.addRingBand(rift.center, "misc", "rings_asteroids0", 256f, 1, Color.gray, 256f, orbitDistance + 350, 100f);

        //Some Random Amounts of Extra Planets
        var rng = MathUtils.getRandomNumberInRange(1,3)
        for (index in 0 until rng)
        {
            orbitDistance += MathUtils.getRandomNumberInRange(500f, 1000f)
            var RandomPlanet = LunaProcGen.generatePlanet(rift, "RiftPlanet", "Planet", orbitDistance);
            RandomPlanet.memoryWithoutUpdate.set("\$RiftPlanet", true)
            RandomPlanet.addTag(RiftData.RiftPlanet)
        }
        var planets = rift.planets

        //Random chance for a moon on a planet
        if (LunaMisc.randomBool())
        {
            var moon = LunaProcGen.generateMoon(planets.get(MathUtils.getRandomNumberInRange(1, planets.size - 1)), "RiftMoon", "Moon", 500f, 3f)
            moon!!.memoryWithoutUpdate.set("\$RiftPlanet", true)
            moon.addTag(RiftData.RiftPlanet)
        }
        //Random chance for another Asteroid Belt
        if (LunaMisc.randomBool())
        {
            rift.addAsteroidBelt(rift.center, 300, orbitDistance + 600, 256f, 100f, 100f, Terrain.ASTEROID_BELT, "");
            rift.addRingBand(rift.center, "misc", "rings_asteroids0", 256f, 1, Color.gray, 256f, orbitDistance + 600, 200f);
        }

        generateRuins(rift, 1, 3)

    }
}