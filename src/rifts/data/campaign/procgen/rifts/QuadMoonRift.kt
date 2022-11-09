package rifts.data.campaign.procgen.rifts

import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.impl.campaign.ids.Terrain
import lunalib.util.LunaMisc
import lunalib.util.LunaProcGen
import rifts.data.campaign.procgen.RiftGenAPI
import rifts.data.campaign.procgen.specs.RiftSpec
import rifts.data.campaign.procgen.specs.StarTypeSpec
import rifts.data.util.RiftData
import java.awt.Color


class QuadMoonRift : RiftGenAPI()
{
    override fun generate(riftSpec: RiftSpec, starSpec: StarTypeSpec)
    {
        var rift: StarSystemAPI = initiateRift(riftSpec, starSpec, 200f, 50f)

        var wormhole = generateRiftWormholes(rift.center, 0f, 0f, 200f, LunaMisc.randomColor(255))

        var moon1 = LunaProcGen.generateMoon(wormhole[0], "RiftMoon", "Moon", 750f,3f)
        moon1!!.addTag(RiftData.RiftPlanet)
        var moon2 = LunaProcGen.generateMoon(wormhole[0], "RiftMoon", "Moon", 900f, 2f)
        moon2!!.addTag(RiftData.RiftPlanet)
        var moon3 = LunaProcGen.generateMoon(wormhole[0], "RiftMoon", "Moon", 1100f, 3f)
        moon3!!.addTag(RiftData.RiftPlanet)
        var moon4 = LunaProcGen.generateMoon(wormhole[0], "RiftMoon", "Moon", 1300f, 3f)
        moon4!!.addTag(RiftData.RiftPlanet)

        //Asteroids at Rifts
        rift.addAsteroidBelt(wormhole[0], 10, 500f, 256f, 100f, 100f, Terrain.ASTEROID_BELT, "");
        rift.addRingBand(wormhole[0], "misc", "rings_asteroids0", 256f, 0, Color.gray, 256f, 500f, 100f);

        rift.addAsteroidBelt(wormhole[0], 100, 1650f, 256f, 100f, 100f, Terrain.ASTEROID_BELT, "");
        rift.addRingBand(wormhole[0], "misc", "rings_asteroids0", 256f, 0, Color.gray, 256f, 1650f, 100f);

        if (LunaMisc.randomBool())
        {
            generateUnknownStation(wormhole[0], 1650f, 0, 3, 5, 10)
        }
        generateRuins(rift, 1, 3)

    }
}