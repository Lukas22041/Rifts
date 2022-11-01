package rifts.data.campaign.procgen.rifts

import com.fs.starfarer.api.campaign.StarSystemAPI
import lunalib.Util.LunaProcGen
import org.lazywizard.lazylib.MathUtils
import rifts.data.campaign.procgen.RiftGenAPI
import rifts.data.campaign.procgen.specs.RiftSpec
import rifts.data.campaign.procgen.specs.StarTypeSpec
import rifts.data.util.RiftStrings


class SupermassiveBlackholeRift : RiftGenAPI()
{
    override fun generate(riftSpec: RiftSpec, starSpec: StarTypeSpec)
    {
        var rift: StarSystemAPI = initiateRift(riftSpec, starSpec, 500f, 200f)

        generateRiftWormholes(rift.center, MathUtils.getRandomNumberInRange(1300f, 1800f), rift.star.spec.atmosphereColor)

        var planet = LunaProcGen.generatePlanet(rift, "RiftPlanet", " Otherwordly Planet", 3500f);
        planet.addTag(RiftStrings.RiftPlanet)

        generateUnknownStation(rift.center, 2000f, 0, 10, 6, 20)

        generateRuins(rift, 0, 1)


    }
}