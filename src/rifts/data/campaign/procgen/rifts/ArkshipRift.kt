package rifts.data.campaign.procgen.rifts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignUIAPI.CoreUITradeMode
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.campaign.econ.MarketAPI.SurveyLevel
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.impl.campaign.ids.Submarkets
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin
import lunalib.Util.LunaMisc
import lunalib.Util.LunaProcGen
import org.lazywizard.lazylib.MathUtils
import rifts.data.campaign.procgen.RiftGenAPI
import rifts.data.campaign.procgen.specs.RiftSpec
import rifts.data.campaign.procgen.specs.StarTypeSpec
import rifts.data.util.RiftStrings
import rifts.data.util.WormholeGenerator
import java.awt.Color


class ArkshipRift : RiftGenAPI()
{

    override fun generate(riftSpec: RiftSpec, starSpec: StarTypeSpec)
    {
        var rift: StarSystemAPI = initiateRift(riftSpec, starSpec, 200f, 50f)

        //var wormhole = generateRiftWormholes(rift.center, 0f, 0f, 200f, LunaMisc.randomColor(255))

        var planet = LunaProcGen.generatePlanet(rift, "ArkshipPlanet", " Otherwordly Planet", 1000f, 400f);
        planet.addTag(RiftStrings.RiftPlanet)
        planet.addTag(RiftStrings.ArkshipPlanet)
        planet.setInteractionImage("illustrations", "survey");

        //Generate Arkship
        val Arkship: SectorEntityToken = rift.addCustomEntity("Arkship","???", "Arkship_Entity", "independent")
        Arkship.setCircularOrbitPointingDown(planet, MathUtils.getRandomNumberInRange(0f, 360f), 400f, 200f)
        Arkship.customDescriptionId = "arkship_inactive"
        Arkship.setInteractionImage("illustrations", "orbital");
        Arkship.memoryWithoutUpdate.set("\$DerelictArkship", true)

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