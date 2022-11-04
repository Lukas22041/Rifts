package rifts.data.campaign.interaction

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.rules.MemKeys
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.combat.MutableShipStatsAPI
import com.fs.starfarer.api.impl.campaign.rulecmd.DismissDialog
import com.fs.starfarer.api.util.Misc
import org.lwjgl.input.Keyboard
import rifts.data.util.RiftStrings
import rifts.data.util.WordRedacter
import java.util.zip.Inflater

data class RuinsLoot(var StrangeMatter: Float, var LanguageSample: Float, var ExoticConstructionMat: Float,  var correctButtonPos: Int)

class RiftPlanetInteraction : InteractionDialogPlugin
{

    lateinit var dialog: InteractionDialogAPI
    private var memoryMap1: MutableMap<String, MemoryAPI> = HashMap()
    lateinit var textPanel: TextPanelAPI
    lateinit var options: OptionPanelAPI
    lateinit var visual: VisualPanelAPI

    override fun init(dialog: InteractionDialogAPI?) {
        if (dialog == null) return
        this.dialog = dialog

        textPanel = dialog.textPanel
        visual = dialog.visualPanel
        options = dialog.optionPanel

        memoryMap1.clear()
        var memory = dialog.interactionTarget.memory
        memoryMap1.put(MemKeys.LOCAL, memory);

        if (dialog.interactionTarget.customInteractionDialogImageVisual != null)
        {
            visual.showImageVisual(dialog.interactionTarget.customInteractionDialogImageVisual)
        }

        textPanel.addPara("The fleet lands at a planet within the Rift. Due to its location, it's impossible to create a Colony here.")

        if (dialog.interactionTarget.hasTag(RiftStrings.riftHasRuins)) ruinsPlanet()
        if (dialog.interactionTarget.hasTag(RiftStrings.ArkshipPlanet)) arkshipPlanet()
        if (dialog.interactionTarget.id == "OriginStarPlanet") originStarPlanet()

        options.addOption("Leave", "LEAVE");
        options.setShortcut("LEAVE", Keyboard.KEY_ESCAPE, false, false, false, true);
    }

    override fun optionSelected(optionText: String?, optionData: Any?) {
        val optionId = optionData as String

        when (optionId)
        {

            "LOOT" ->
            {
                dialog.interactionTarget.removeTag(RiftStrings.riftHasRuins)
                options.clearOptions()

                textPanel.addPara("You send your crew to explore the ruins of the planet. After some searching, they come back with their discoveries")

                val salvage = Global.getFactory().createCargo(true)
                val loot = dialog.interactionTarget.memoryWithoutUpdate.get(RiftStrings.riftSalvageSeed) as RuinsLoot

                salvage.addCommodity(RiftStrings.strangeMatterID, loot.StrangeMatter)
                salvage.addCommodity(RiftStrings.languageSampleID, loot.LanguageSample)
                salvage.addCommodity(RiftStrings.languageSampleID, loot.ExoticConstructionMat)

                if (!salvage.isEmpty)
                {
                    dialog.getVisualPanel().showLoot("Salvaged", salvage, false, true, true, null)
                }

                options.addOption("Leave", "LEAVE");
                options.setShortcut("LEAVE", Keyboard.KEY_ESCAPE, false, false, false, true);
            }

            "UNTRANSLATED" ->
            {
                val loot = dialog.interactionTarget.memoryWithoutUpdate.get(RiftStrings.riftSalvageSeed) as RuinsLoot
                val redacter = WordRedacter()
                var selfDestructAdded = false

                textPanel.clear()
                options.clearOptions()
                textPanel.addPara("The salvage team makes it towards the ruins. Once they made it on site, they discover a room that appears to be a control room. They find multiple Buttons in the Alien Language. \n\n" +
                        "The Facility does not seem to have a lot of power left, its likely that we will only be able to select one of the buttons.")

                for (index in 0..2)
                {
                    if (index == loot.correctButtonPos)
                    {
                        options.addOption(redacter.replace("Open Storage Room"), "UNTRANSLATED_STORAGE")
                    }
                    else if (!selfDestructAdded)
                    {
                        options.addOption(redacter.replace("Activate Emergency Self-Destruct"), "UNTRANSLATED_SELFDESTRUCT")
                        selfDestructAdded = true
                    }
                    else
                    {
                        options.addOption(redacter.replace("Access the Data Archive"), "UNTRANSLATED_ARCHIVE")
                    }
                }

                options.addOption("Leave", "LEAVE");
                options.setShortcut("LEAVE", Keyboard.KEY_ESCAPE, false, false, false, true);
            }

            "UNTRANSLATED_STORAGE" ->
            {
                options.clearOptions()
                dialog.interactionTarget.removeTag(RiftStrings.riftHasRuins)
                val salvage = Global.getFactory().createCargo(true)
                val loot = dialog.interactionTarget.memoryWithoutUpdate.get(RiftStrings.riftSalvageSeed) as RuinsLoot

                salvage.addCommodity(RiftStrings.strangeMatterID, loot.StrangeMatter)
                salvage.addCommodity(RiftStrings.strangeMatterID, loot.ExoticConstructionMat)

                if (!salvage.isEmpty)
                {
                    dialog.getVisualPanel().showLoot("Salvaged", salvage, false, true, true, null)
                }

                options.addOption("Leave", "LEAVE");
                options.setShortcut("LEAVE", Keyboard.KEY_ESCAPE, false, false, false, true);
            }
            "UNTRANSLATED_ARCHIVE" ->
            {
                options.clearOptions()
                dialog.interactionTarget.removeTag(RiftStrings.riftHasRuins)
                val salvage = Global.getFactory().createCargo(true)
                val loot = dialog.interactionTarget.memoryWithoutUpdate.get(RiftStrings.riftSalvageSeed) as RuinsLoot

                salvage.addCommodity(RiftStrings.languageSampleID, loot.LanguageSample)

                if (!salvage.isEmpty)
                {
                    dialog.getVisualPanel().showLoot("Salvaged", salvage, false, true, true, null)
                }

                options.addOption("Leave", "LEAVE");
                options.setShortcut("LEAVE", Keyboard.KEY_ESCAPE, false, false, false, true);
            }
            "UNTRANSLATED_SELFDESTRUCT" ->
            {
                textPanel.clear()
                options.clearOptions()
                dialog.interactionTarget.removeTag(RiftStrings.riftHasRuins)
                val loot = dialog.interactionTarget.memoryWithoutUpdate.get(RiftStrings.riftSalvageSeed) as RuinsLoot
                val redacter = WordRedacter()


                textPanel.addPara("Your crew presses button. \n\n" +
                        "Immediately, sensors reveal a rapid increase in heat around the facility, until it suddenly blows up, its shockwave being observable even from Orbit.")

                textPanel.addPara("> The explosion cost the life of all ${(loot.StrangeMatter * 6).toInt()} crewman on the surface", Misc.getNegativeHighlightColor(), "${(loot.StrangeMatter * 6).toInt()}")

                Global.getSector().playerFleet.cargo.removeCrew((loot.StrangeMatter * 6).toInt())

                options.addOption("Leave", "LEAVE");
                options.setShortcut("LEAVE", Keyboard.KEY_ESCAPE, false, false, false, true);
            }

            "LEAVE" ->
            {
                DismissDialog().execute(null, dialog, null, memoryMap)
            }
        }


    }

    override fun optionMousedOver(optionText: String?, optionData: Any?) {

    }

    fun ruinsPlanet()
    {
        var memory = dialog.interactionTarget.memory

        if (memory.get(RiftStrings.ruinsTypeKey) == null) return
        var ruintype = memory.get(RiftStrings.ruinsTypeKey) as RiftStrings.ruinTypes

        when (ruintype)
        {
            RiftStrings.ruinTypes.Normal ->
            {
                textPanel.addPara("Scans of this planet reveal an alien ruin on the surface. We could send a salvage team down to loot through it")
                options.addOption("Loot the Ruins", "LOOT")
            }
            RiftStrings.ruinTypes.Untranslated ->
            {
                textPanel.addPara("After scanning the planet, we discovered a small ruin on it. We cant quite detect what this ruin holds from orbit.")
                options.addOption("Send a salvage team down", "UNTRANSLATED")
            }
            RiftStrings.ruinTypes.HullmodIntegration ->
            {

            }
        }


    }

    fun arkshipPlanet()
    {
        var redactor = WordRedacter()

        textPanel.addPara("Scans of this planet revealed something that resembles a ruin. " +
                "Upon arrival, the salvage crew discovers a Terminal that seems to display log entries in an alien language.\n\n")

        var text2: String = redactor.replace("\"Origin Stars shine in a great, green light.\n" +
                "They have some special characteristics, that make them essential to our species. They exert strong forces, bending spacetime around them. \n" +
                "This instability however has a use, by igniting some strange matter, gained by siphoning material from the star, you can create a wormhole between dimensions\n" +
                "Its still not completly understood, but this process also seems to bring Universes closer together, causing wormholes to appear on their own.\n" +
                "\"")

        var label = textPanel.addPara(text2, Misc.getBasePlayerColor())

        dialog.interactionTarget.addTag(RiftStrings.RecoveredLanguage)
    }

    fun originStarPlanet()
    {
        var redactor = WordRedacter()

        textPanel.addPara("Scans of this planet revealed something that resembles a ruin. " +
                "Upon arrival, the salvage crew discovers a Terminal that seems to display log entries in an alien language.\n\n")

        var text: String = redactor.replace("\"We had already discovered the requirements to creating a wormhole, but creating a wormhole towards an entirely new Universe was unknown even to us." +
                "You cant just ignite the material in conventional methods, you need to further bend the spacetime around for it to suceed. Luckily we already had the infrastructure for this." +
                "Once we understood the process, we quickly realized the possibilities. \"")

        var label = textPanel.addPara(text, Misc.getBasePlayerColor())

        dialog.interactionTarget.addTag(RiftStrings.RecoveredLanguage)
    }

    override fun advance(amount: Float) {

    }

    override fun backFromEngagement(battleResult: EngagementResultAPI?) {

    }

    override fun getContext(): Any? {
        return null
    }

    override fun getMemoryMap(): MutableMap<String, MemoryAPI> {
        return memoryMap1
    }

}