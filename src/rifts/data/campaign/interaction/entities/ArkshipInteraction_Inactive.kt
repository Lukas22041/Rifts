package rifts.data.campaign.interaction.entities

import com.fs.starfarer.api.Global
import lunalib.extension.LunaInteraction
import lunalib.extension.oCallback
import rifts.data.scripts.ArkshipWarp
import rifts.data.util.RiftData

class ArkshipInteraction_Inactive : LunaInteraction()
{
    override fun mainPage()
    {
        textPanel.addPara("As you explore the rift, your crew stumbles upon a massive construct orbiting a lone Planet. While it has the size of a station, it's shape resembles that of a ship.\n" +
                "\n" +
                "As the fleet docks at the entity, reports from the engineering team come in. This place is build in a similar manner as to how Stations are build within our sector. However, there was one oddity found within the Station. There seems to be a massive reactor room, far exceeding what a normal Station would require. Analysis confirms that the Station must have still been powered recently.\n" +
                "\n" +
                "Our Engineers found traces of some strange matter within the reactor, they currently suspect it to be the fuel of the whole Station, if we bring enough, we may be able to reactivate the construct and discover its purpose."
        )

        textPanel.highlightInLastPara("massive construct", "massive reactor", "wormholes", "strange matter")
        var metConditions = createCostPanel(RiftData.strangeMatterID, 50, true)

        addOption("Bring the construct online", "BRING_ONLINE1")
        if (!metConditions) setEnabled("BRING_ONLINE1", false)

        if (Global.getSettings().isDevMode) addOption("Bring online (Devmode)", oCallback { bringOnline1()})

        addLeaveOption()
    }

    override fun optionSelected(optionText: String?, optionData: Any?) {
        super.optionSelected(optionText, optionData)

        if (optionData == "BRING_ONLINE1") bringOnline1()
    }

    fun bringOnline1()
    {
        Global.getSector().playerFleet.cargo.removeCommodity(RiftData.strangeMatterID, 50f)

        clear()

       addPara("With the strange matter supplied, we were able to re-ignite this constructions generator.\n" +
                "\n" +
                "Suddenly, the fleets instruments start screaming wildy, a dangerous increase in spacetime distortion is starting to emerge from the the construct, and then..." )

        addOption("???", oCallback { bringOnline2()});
    }

    fun bringOnline2()
    {
        closeDialog()
        Global.getSector().addScript(ArkshipWarp(interactionTarget, false))
        interactionTarget.removeTag(RiftData.ArkshipInactive)
        interactionTarget.addTag(RiftData.ArkshipWarped)

    }
}