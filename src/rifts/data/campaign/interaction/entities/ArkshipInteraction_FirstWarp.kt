package rifts.data.campaign.interaction.entities

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.Script
import com.fs.starfarer.api.util.Misc
import lunalib.extension.LunaInteraction
import lunalib.extension.oCallback
import rifts.data.campaign.intel.ArkshipIntel
import rifts.data.util.RiftData

class ArkshipInteraction_FirstWarp : LunaInteraction()
{
    override fun mainPage()
    {
        addPara("...in a moment, our fleet found itself engulfed within the waves of massive wormhole. \n" +
                "\n" +
                "As we now obviously know, the massive reactor build in to this station seems to generate Wormholes at command. And more importantly, we are currently in command of said device." +
                "\n\nIt's not all good news though, engineering reports that our careless activation of the reactor may have damaged its components somewhat. It is unlikely that future wormholes remain permanent with the current damage, but in the short time they stay open, we should be able to move the station through them.",
            highlightColor,
            "massive reactor", "damaged", "unlikely", "permanent")

        addOption("Continue", oCallback { afterWarp1() })
    }

    override fun optionSelected(optionText: String?, optionData: Any?) {
        super.optionSelected(optionText, optionData)
    }

    fun afterWarp1()
    {
        clear()

        addPara("We went and installed a Communication device in to the station, aslong as we keep some crew arround, we should be able to start the device remotely.\n" +
                "We also need to make sure to store some of that strange matter within the cargo holds, otherwise the reactor wont have enough power to create more wormholes.", Misc.getHighlightColor(), "strange matter")

        addPara("As we were finishing up the setup, engineering reported on their analysis on the now active ship.\n" +
                "They managed to find a few broken hulls, resembling those of the ships we encountered so far within the rifts inside of the Hangar. They arent functional, but we may be able to repair them. \n" +
                "\nAdditionaly, they disovered a Terminal which seems to hold some kind of writing, but the language is completly foreign. But, we may be able to use it decipher some of it", Misc.getHighlightColor(),
             "broken hulls", "repair",  "decipher")

        addPara("> Gained \"Wormhole\" Ability", Misc.getPositiveHighlightColor(), "> Gained \"Wormhole\" Ability")
        addPara("> Added \"Arkship\" intel to the Rifts Intel Category", Misc.getPositiveHighlightColor(), "> Added \"Arkship\" intel to the Rifts Intel Category")

        interactionTarget.name =  "The Arkhip"
        interactionTarget.market.name = "The Arkship"
        interactionTarget.customDescriptionId = "arkship"

        addOption("Finish setup", oCallback { afterWarp2() });
        RiftData.arkshipName = "Arkhip"
        RiftData.arkshipPrefix = "The"
        val abilityId = "arkship_wormhole"
        sector.characterData.addAbility(abilityId)
        sector.characterData.memoryWithoutUpdate["\$ability:$abilityId", true] = 0f
        sector.intelManager.addIntel(ArkshipIntel())
    }

    fun afterWarp2()
    {
        interactionTarget.removeTag(RiftData.ArkshipWarped)
        closeDialog()
    }
}