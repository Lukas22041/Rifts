package rifts.data.campaign.interaction.planets

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.CoreInteractionListener
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext
import lunalib.extension.LunaInteraction
import lunalib.extension.oCallback
import rifts.data.util.RiftRuinsData
import rifts.data.util.RiftData
import rifts.data.util.RuinsLoot

class PlanetRuins_Common() : LunaInteraction(), CoreInteractionListener
{

    var defenders: CampaignFleetAPI? = null
    var context2 = FleetEncounterContext()

    override fun mainPage()
    {
        addPara("The fleet arrives at a planet within the Rift. Due to its location, it's impossible to create a Colony here.")

        defenders = targetMemory.get("\$defenderFleet") as CampaignFleetAPI?

        if (defenders == null)
        {
            addPara("Scans of this planet reveal structures that resemble ruins. ")
            addOption("Explore the ruins", oCallback { exploreRuins() })
            addLeaveOption()
        }
        else
        {
            addPara("Scans of this planet reveal structures that resemble ruins, however, the same scans also revealed defensive chirality forces within the atmosphere.")
            triggerDefenders()
        }
    }

    override fun optionSelected(optionText: String?, optionData: Any?) {
        super.optionSelected(optionText, optionData)
    }



    fun exploreRuins()
    {
        clear()

        textPanel.addPara("You send your crew to explore the ruins of the planet. After some exploration, they come back with sites promising adequate loot in their vicinity.")
        addOption("Send a salvage teams towards the sites", oCallback { salvageRuins() })

        addLeaveOption()
    }

    fun salvageRuins()
    {
        interactionTarget.removeTag(RiftRuinsData.commonRuinsTag)

        val salvage = Global.getFactory().createCargo(true)
        val loot = targetMemory.get(RiftRuinsData.salvageDataMemory) as RuinsLoot

        salvage.addCommodity(RiftData.strangeMatterID, loot.StrangeMatter)
        salvage.addCommodity(RiftData.languageSampleID, loot.LanguageSample)
        salvage.addCommodity(RiftData.exoticConstructionID, loot.ExoticConstructionMat)

        dialog.getVisualPanel().showLoot("Loot", salvage, false, true, true, this)
    }

    override fun coreUIDismissed() {
        closeDialog()
    }

    override fun defeatedDefenders() {
        exploreRuins()
    }
}

