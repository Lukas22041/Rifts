package rifts.data.campaign.interaction.entities

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.rules.MemKeys
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.impl.campaign.rulecmd.DismissDialog
import com.fs.starfarer.api.util.Misc
import rifts.data.campaign.intel.RiftsLogIntel
import rifts.data.campaign.intel.WormholeIntel
import rifts.data.util.RiftStrings


class FirstWormholeInteraction : InteractionDialogPlugin
{

    private var dialog: InteractionDialogAPI? = null

    override fun init(dialog: InteractionDialogAPI?) {
        if (dialog == null) return
        this.dialog = dialog

       /* dialog.textPanel.addPara("At a moment, the star we only just discovered, went supernova. Despite the massive forces involved, the fleet appears to be mostly unaffected\n\n" +
                "Following that, our sensors picked up a new signal close to the core of the star. " +
                "Its an object that exerts similar properties to that of a Jumpoint. However...what is detected from beyond is unlike anything ever observed, seemingly being out of this world.\n " +
                "\n" +
                "We are detecting new signatures like those things, lets call them \"Rifts\", all across the sector. It's likely that we will find more of them within other systems from now on.\n" +
                "If we want to discover the origin of this strange starsystem, we may find our answers somewhere within those rifts.",
            Misc.getHighlightColor(),
            "supernova", "new signal", "similar properties to that of a Jumpoint", "unlike", "being out of this world", "Rifts", " all across the sector", "other systems", "answers somewhere within those rifts")
*/
        dialog.textPanel.addPara("" +
                "Our sensors picked up a new signal close to the core of this strange star. " +
                "Its an object that exerts similar properties to that of a Jumpoint. Moments later, we discovered a fleet emerging from it. Its properties dont allign with that of any of the sectors hullspecs.\n " +
                "\n" +
                "Oddly enough, we are detecting new signatures resembling those entities across the sector. We marked systems with those signatures on the map.",
            Misc.getHighlightColor(),
            "sensors", "new signal", "fleet", "signatures", "across the sector")



        dialog.optionPanel.addOption("Enter the Dimensional Rift", "enter")

    }

    override fun optionSelected(optionText: String?, optionData: Any?) {

        val optionId = optionData as String

        var memoryMap: MutableMap<String, MemoryAPI> = HashMap()
        memoryMap.clear()
        var memory = dialog!!.interactionTarget.memory
        memoryMap.put(MemKeys.LOCAL, memory);

        if (optionId.equals("enter"))
        {
            DismissDialog().execute(null, dialog, null, memoryMap)
            Global.getSector().doHyperspaceTransition(Global.getSector().playerFleet, dialog!!.interactionTarget, JumpPointAPI.JumpDestination(dialog!!.interactionTarget.memoryWithoutUpdate.get("\$WormholeDestination") as SectorEntityToken,"debug"), 2f);
            Global.getSector().memoryWithoutUpdate.set("\$RiftHasBeenUsed", true)

            //Generates Intel Page for all Wormholes
            val systems = Global.getSector().starSystems

            for (system in systems)
            {
                if (system.hasTag(RiftStrings.hasWormhole))
                {
                    Global.getSector().intelManager.addIntel(WormholeIntel(system))
                }
            }
            Global.getSector().intelManager.addIntel(RiftsLogIntel())
        }
    }

    override fun optionMousedOver(optionText: String?, optionData: Any?) {
    }

    override fun advance(amount: Float) {
    }

    override fun backFromEngagement(battleResult: EngagementResultAPI?) {
    }

    override fun getContext(): Any? {
        return null
    }

    override fun getMemoryMap(): MutableMap<String, MemoryAPI>? {
        return null
    }


}