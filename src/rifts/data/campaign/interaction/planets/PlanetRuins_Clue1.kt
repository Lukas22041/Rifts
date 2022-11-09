package rifts.data.campaign.interaction.planets

import com.fs.starfarer.api.util.Misc
import lunalib.extension.LunaInteraction
import lunalib.extension.oCallback
import rifts.data.util.RiftRuinsData
import rifts.data.util.WordRedacter

class PlanetRuins_Clue1() : LunaInteraction()
{

    var redactor = WordRedacter()

    companion object
    {
        var Clue1Text = "\"Origin Stars shine in a great, green light.\n" +
                "They have some special characteristics, that make them essential to our species. They exert strong forces, bending spacetime around them. \n\n" +
                "This instability however has a use, by igniting some strange matter, gained by siphoning material from the star, you can create a wormhole between dimensions.\n" +
                "Its still not completly understood, but this process also seems to bring Universes closer together, causing wormholes to appear on their own. \""
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
        var text: String = redactor.replace(Clue1Text)

        addPara(text, Misc.getBasePlayerColor())

        addPara("\nThe crew transcribe the writing into the fleets logs.\n")

        interactionTarget.removeTag(RiftRuinsData.cluePlanet1Tag)
        addLeaveOption()
    }
}

