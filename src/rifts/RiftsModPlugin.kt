package rifts

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.FactionAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.fleet.FleetMemberType
import com.fs.starfarer.api.graphics.SpriteAPI
import com.fs.starfarer.api.impl.campaign.ids.Personalities
import rifts.data.scripts.RiftsCampaignPlugin
import rifts.data.scripts.RiftsCampaignScript
import rifts.data.campaign.procgen.specs.SpecLoader
import rifts.data.campaign.procgen.world.OriginSystem

class RiftsModPlugin : BaseModPlugin()
{
    override fun onNewGameAfterEconomyLoad()
    {
        var faction: FactionAPI = Global.getSector().getFaction("chirality")
        var factions = Global.getSector().allFactions

        for (hostileFaction in factions)
        {
            if (hostileFaction.id == "chirality") continue;
            faction.setRelationship(hostileFaction.id, -100f)
        }

        addDerelictsToArkship()
        addUnknownWords()
        OriginSystem.generate()
    }

    fun addDerelictsToArkship()
    {
        var memory = Global.getSector().memoryWithoutUpdate
        var fleet = Global.getFactory().createEmptyFleet("chirality", "Derelict Ships", false)

        for (i in 0..3)
        {
            var Dune = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "rifts_dune_Hull")
            Dune.getRepairTracker().setCR(0.70f);
            fleet.fleetData.addFleetMember(Dune)
        }
        for (i in 0..2)
        {
            var Opera = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "rifts_opera_Hull")
            Opera.getRepairTracker().setCR(0.70f);
            fleet.fleetData.addFleetMember(Opera)
        }
        for (i in 0..1)
        {
            var Opera = Global.getFactory().createFleetMember(FleetMemberType.SHIP, "rifts_phenix_Hull")
            Opera.getRepairTracker().setCR(0.70f);
            fleet.fleetData.addFleetMember(Opera)
        }


        memory.set("\$rifts_arkship_hangar", fleet)
    }

    //adds Unknown Words in to the Sectors Memory
    fun addUnknownWords()
    {
        var memory =Global.getSector().memoryWithoutUpdate
        var unknownWords = listOf<String>("Origin", "Seeds", "Dimension", "Star", "Universe", "Life", "Bond", "Worlds", "Core", "Strange Matter", "Ignite", "Distortion",
            "Travelling", "Ship", "Wormhole", "System", "Cycle", "enemy", "hull", "speed", "shield", "stats", "limit", "efficiency", "Data", "Archive", "Storage",
            "Self-Destruct", "Emergency")
        memory.set("\$rifts_unknownWords", unknownWords)
        memory.set("\$rifts_totalUnknownWords", unknownWords)
        memory.set("\$rifts_languageBasicWords", false)
    }

    override fun onGameLoad(newGame: Boolean)
    {
        var plugin = RiftsCampaignPlugin()
        Global.getSector().registerPlugin(plugin)
        Global.getSector().addTransientScript(RiftsCampaignScript())
    }


    override fun onApplicationLoad()
    {
        SpecLoader.loadRiftSpec()
        SpecLoader.loadStarTypeSpec()
    }
}