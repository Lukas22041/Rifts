package rifts.data.campaign.procgen.rifts

import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.impl.campaign.ids.Terrain
import lunalib.util.LunaMisc
import lunalib.util.LunaProcGen
import org.lazywizard.lazylib.MathUtils
import rifts.data.campaign.procgen.RiftGenAPI
import rifts.data.campaign.procgen.specs.RiftSpec
import rifts.data.campaign.procgen.specs.StarTypeSpec
import rifts.data.util.RiftData
import java.awt.Color

class ChiralityRift : RiftGenAPI()
{
    override fun generate(riftSpec: RiftSpec, starSpec: StarTypeSpec)
    {
        var rift: StarSystemAPI = initiateRift(riftSpec, starSpec, 800f, 100f)
        rift.name = "Chirality System"

        var orbitDistance = MathUtils.getRandomNumberInRange(2500f, 3000f)

        rift.addRingBand(rift.center, "misc", "rings_asteroids0", 256f, 0, Color.gray, 256f, 1100f, 100f);
        rift.addRingBand(rift.center, "misc", "rings_asteroids0", 256f, 0, Color.gray, 256f, 1300f, 120f);
        rift.addRingBand(rift.center, "misc", "rings_asteroids0", 256f, 0, Color.gray, 256f, 1500f, 140f);

        //Some Random Amounts of Extra Planets
        var rng = MathUtils.getRandomNumberInRange(3,4)
        for (index in 0 until rng)
        {
            orbitDistance += MathUtils.getRandomNumberInRange(600f, 1000f)
            var RandomPlanet = LunaProcGen.generatePlanet(rift, "RiftPlanet", "Planet", orbitDistance);
            RandomPlanet.memoryWithoutUpdate.set("\$RiftPlanet", true)
            RandomPlanet.addTag(RiftData.RiftPlanet)
            generateUnknownStation(RandomPlanet, 500f, 2, 4, 30, 30)

            if (LunaMisc.randomBool())
            {
                rift.addRingBand(rift.center, "misc", "rings_asteroids0", 200f, 0, Color.gray, 200f, orbitDistance + 300, 100f);
            }
        }
        var planets = rift.planets

        val ChiralArkship: SectorEntityToken = rift.addCustomEntity("Chiral_Arkship","Chiral Wormhole Ship", "Arkship_Entity", "chirality")
        ChiralArkship.setCircularOrbitPointingDown(planets.get(MathUtils.getRandomNumberInRange(1, planets.size - 1)), MathUtils.getRandomNumberInRange(0f, 360f), 550f, 200f)
        ChiralArkship.customDescriptionId = ""
        ChiralArkship.setInteractionImage("illustrations", "orbital");

        //Random chance for another Asteroid Belt
        if (LunaMisc.randomBool())
        {
            rift.addAsteroidBelt(rift.center, 300, orbitDistance + 600, 256f, 100f, 100f, Terrain.ASTEROID_BELT, "");
            rift.addRingBand(rift.center, "misc", "rings_asteroids0", 256f, 1, Color.gray, 256f, orbitDistance + 600, 200f);
        }
    }
}