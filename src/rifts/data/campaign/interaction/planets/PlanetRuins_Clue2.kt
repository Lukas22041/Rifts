package rifts.data.campaign.interaction.planets

import com.fs.starfarer.api.util.Misc
import lunalib.Util.LunaInteraction
import lunalib.Util.oCallback
import rifts.data.util.RiftRuinsData
import rifts.data.util.RiftStrings
import rifts.data.util.WordRedacter

class PlanetRuins_Clue2() : LunaInteraction()
{

    var redactor = WordRedacter()

    companion object
    {
        var Clue2Text = "\"We had already discovered the requirements to creating a wormhole, but creating a wormhole towards an entirely new Universe was unknown even to us. " +
                "You cant just ignite the material in conventional methods, you need to further bend the spacetime around for it to suceed. Luckily we already had the infrastructure for this. " +
                "Once we understood the process, we quickly realized the possibilities. \""
    }

    override fun mainPage()
    {
        addPara("The fleet arrives at a planet within the Rift. Due to its location, it's impossible to create a Colony here.")
        addPara("Scans of this planet revealed something that resembles a ruin. ")

        addOption("Explore the ruins", oCallback { exploreRuins() })
        addLeaveOption()
    }

    override fun optionSelected(optionText: String?, optionData: Any?) {
        super.optionSelected(optionText, optionData)
    }

    fun exploreRuins()
    {
        clear()

        addPara("Upon arrival, the salvage crew discovers a Terminal that seems to display log entries in an alien language.\n\n")
        var text: String = redactor.replace(Clue2Text)

        addPara(text, Misc.getBasePlayerColor())

        addPara("\nThe crew transcribe the writing into the fleets logs.\n")

        interactionTarget.removeTag(RiftRuinsData.cluePlanet2Tag)
        addLeaveOption()
    }
}

