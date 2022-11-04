package rifts.data.util

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.LocationAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3
import com.fs.starfarer.api.impl.campaign.ids.Abilities
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantAssignmentAI
import java.util.*

object ChiralitySpawner
{
    @JvmStatic
    fun spawnChiralFleet(loc: SectorEntityToken, fleetType: String = FleetTypes.PATROL_LARGE, points: Float = 120f) : CampaignFleetAPI
    {
        val random = Random()

        val params = FleetParamsV3(loc.market,
            loc.locationInHyperspace,
            "chirality",
            5f,
            fleetType,
            points,  // combatPts
            0f,  // freighterPts
            0f,  // tankerPts
            0f,  // transportPts
            0f,  // linerPts
            0f,  // utilityPts
            0f // qualityMod
        )

        params.random = random
        val fleet = FleetFactoryV3.createFleet(params)

        fleet.addAbility(Abilities.TRANSPONDER)
        fleet.getAbility(Abilities.TRANSPONDER).activate()
        val location: LocationAPI = loc.getContainingLocation()
        location.addEntity(fleet)

        fleet.setLocation(loc.getLocation().x, loc.getLocation().y)
        fleet.facing = random.nextFloat() * 360f

        fleet.addScript(RemnantAssignmentAI(fleet, loc.getContainingLocation() as StarSystemAPI, loc))


        val data = SalvageEntityGenDataSpec.DropData()

        var chances = 0
        for (member in fleet.fleetData.membersListCopy) {
            chances =
                if (member.isCapital) +16 else if (member.isCruiser) +12 else if (member.isDestroyer) +8 else if (member.isFrigate) +4 else +2
        }

        data.group = "rifts_drop"
        data.chances = chances * 5
        fleet.addDropRandom(data)

        return fleet
    }
}