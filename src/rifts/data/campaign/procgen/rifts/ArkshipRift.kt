package rifts.data.campaign.procgen.rifts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.campaign.econ.MarketAPI.SurveyLevel
import com.fs.starfarer.api.impl.campaign.ids.Submarkets
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin
import lunalib.util.LunaProcGen
import org.lazywizard.lazylib.MathUtils
import rifts.data.campaign.procgen.RiftGenAPI
import rifts.data.campaign.procgen.specs.RiftSpec
import rifts.data.campaign.procgen.specs.StarTypeSpec
import rifts.data.util.RiftRuinsData
import rifts.data.util.RiftData
import java.awt.Color


class ArkshipRift : RiftGenAPI()
{

    override fun generate(riftSpec: RiftSpec, starSpec: StarTypeSpec)
    {
        var rift: StarSystemAPI = initiateRift(riftSpec, starSpec, 100f, 50f)

        rift.addRingBand(rift.center, "misc", "rings_dust0", 256f, 0, Color.gray, 256f, 250f, 100f);
        rift.addRingBand(rift.center, "misc", "rings_dust0", 256f, 0, Color.gray, 256f, 1800f, 100f);
        rift.addRingBand(rift.center, "misc", "rings_ice0", 256f, 0, Color.gray, 256f, 1800f, 100f);

        var planet = LunaProcGen.generatePlanet(rift, "ArkshipPlanet", " Otherwordly Planet", 1000f, 400f);
        planet.addTag(RiftData.RiftPlanet)
        planet.addTag(RiftRuinsData.cluePlanet1Tag)
        planet.setInteractionImage("illustrations", "survey");

        //Generate Arkship
        val Arkship: SectorEntityToken = rift.addCustomEntity("Arkship","???", "Arkship_Entity", "independent")
        Arkship.setCircularOrbitPointingDown(planet, MathUtils.getRandomNumberInRange(0f, 360f), 400f, 200f)
        Arkship.customDescriptionId = "arkship_inactive"
        Arkship.setInteractionImage("illustrations", "orbital");
        Arkship.addTag(RiftData.ArkshipInactive)

        //Arkship.getMemoryWithoutUpdate().set("\$abandonedStation", true)
        val market = Global.getFactory().createMarket("TestID2", Arkship.getName(), 0)
        market.surveyLevel = SurveyLevel.FULL
        market.primaryEntity = Arkship
        market.factionId = Arkship.faction.id
        market.addSubmarket(Submarkets.SUBMARKET_STORAGE)
        market.isPlanetConditionMarketOnly = false
        (market.getSubmarket(Submarkets.SUBMARKET_STORAGE).plugin as StoragePlugin).setPlayerPaidToUnlock(true)
        Arkship.setMarket(market)
        Arkship.getMemoryWithoutUpdate().unset("\$tradeMode")
    }
}