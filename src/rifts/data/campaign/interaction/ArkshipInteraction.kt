package rifts.data.campaign.interaction

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.CargoAPI.CargoItemType
import com.fs.starfarer.api.campaign.rules.MemKeys
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.impl.campaign.rulecmd.DismissDialog
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.util.Misc
import data.scripts.util.MagicSettings
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.input.Keyboard
import rifts.data.scripts.ArkshipWarp
import rifts.data.util.RiftStrings
import rifts.data.util.WordRedacter
import java.awt.Color


class ArkshipInteraction : InteractionDialogPlugin
{

    lateinit var dialog: InteractionDialogAPI
    private var memoryMap1: MutableMap<String, MemoryAPI> = HashMap()
    lateinit var textPanel: TextPanelAPI
    lateinit var options: OptionPanelAPI
    lateinit var visual: VisualPanelAPI

    var customName = MagicSettings.getString("rifts", "custom_ship_name")

    override fun init(dialog: InteractionDialogAPI?) {
        if (dialog == null) return
        this.dialog = dialog

        textPanel = dialog.textPanel
        visual = dialog.visualPanel
        options = dialog.optionPanel

        textPanel.clear()
        options.clearOptions()

        memoryMap1.clear()
        var memory = dialog.interactionTarget.memory
        memoryMap1.put(MemKeys.LOCAL, memory);

        visual.showImageVisual(dialog.interactionTarget.customInteractionDialogImageVisual)

        if (dialog.interactionTarget.memoryWithoutUpdate.get("\$DerelictArkship") != null) inactiveArkshipDialog()
        else if (dialog.interactionTarget.memoryWithoutUpdate.get("\$ArkshipFirstJump") != null) afterFirstJumpInteraction()
        else activeArkshipDialog()


    }

    fun activeArkshipDialog()
    {
        dialog.textPanel.addPara("The fleet comes to a stop at ${dialog.interactionTarget.name}, crew can be seen preparing the reactor for its next use." +
                "While we are here, we can make use of the ships Logistical Compartments, or make use of its other features.")

        options.addOption("Store Cargo in ${dialog.interactionTarget.name}", "CARGO")
        options.addOption("Store Ships in ${dialog.interactionTarget.name}", "FLEET")
        options.addOption("Refit Ships in your Fleet", "REFIT")
        options.setShortcut("CARGO", Keyboard.KEY_I, false, false, false, true);
        options.setShortcut("FLEET", Keyboard.KEY_F, false, false, false, true);
        options.setShortcut("REFIT", Keyboard.KEY_R, false, false, false, true);
        options.addOption("Rename ${dialog.interactionTarget.name}", "RENAME")

        if (!(Global.getSector().memoryWithoutUpdate.get("\$rifts_arkship_hangar") as CampaignFleetAPI).isEmpty)
        {
            options.addOption("Restore Ships in the Hangar", "HANGAR")
        }
        if (Global.getSector().memoryWithoutUpdate.get("\$rifts_learnedAllWords") == null)
        {
            options.addOption("Decipher Language", "DECIPHER")
        }

        options.addOption("Leave", "LEAVE");
        options.setShortcut("LEAVE", Keyboard.KEY_ESCAPE, false, false, false, true);

    }

    fun afterFirstJumpInteraction()
    {
        textPanel.addPara("...in a moment, our fleet found itself engulfed within the waves of massive wormhole. \n" +
                "\n" +
                "As we now obviously know, the massive reactor build in to this station seems to generate Wormholes at command. And more importantly, we are currently in command of said device." +
                "\n\nIt's not all good news though, engineering reports that our careless activation of the reactor may have damaged its components somewhat. It is unlikely that future wormholes remain permanent with the current damage, but in the short time they stay open, we should be able to move the station through them.",
            Misc.getHighlightColor(),
            "massive reactor", "damaged", "unlikely", "permanent")

        dialog.interactionTarget.memoryWithoutUpdate.unset("\$ArkshipFirstJump")
        options.addOption("Continue", "AFTER_FIRST_WARP")
    }

    fun inactiveArkshipDialog()
    {
        textPanel.addPara("As you explore the rift, your crew stumbles upon a massive construct orbiting a lone Planet. While it has the size of a station, it's shape resembles that of a ship.\n" +
                "\n" +
                "As the fleet docks at the entity, reports from the engineering team come in. This place is build in a similar manner as to how Stations are build within our sector. However, there was one oddity found within the Station. There seems to be a massive reactor room, far exceeding what a normal Station would require. Analysis confirms that the Station must have still been powered recently.\n" +
                "\n" +
                "Our Engineers found traces of some strange matter within the reactor, they currently suspect it to be the fuel of the whole Station, if we bring enough, we may be able to reactivate the construct and discover its purpose."
                )

        textPanel.highlightInLastPara("massive construct", "massive reactor", "wormholes", "strange matter")

        var cost: ResourceCostPanelAPI = textPanel.addCostPanel("Cost", 67f, Misc.getBasePlayerColor(), Color.DARK_GRAY)
        var cargo = Global.getSector().playerFleet.cargo

        val required: Int = 50
        val available: Int = cargo.getCommodityQuantity("strange_matter").toInt()
        var color: Color? = Misc.getPositiveHighlightColor()
        var metConditions = true

        if (required > cargo.getQuantity(CargoItemType.RESOURCES, "strange_matter")) {
            color = Misc.getNegativeHighlightColor();
            metConditions = false
        }

        cost.setNumberOnlyMode(true);
        cost.setWithBorder(false);
        cost.setAlignment(Alignment.LMID);

        cost.addCost("strange_matter", "" + required + " (" + available + " available)", color);
        cost.update()

        options.addOption("Bring the construct online", "BRING_ONLINE")
        if (!metConditions) options.setEnabled("BRING_ONLINE", false )

        options.addOption("Leave", "LEAVE");
        options.setShortcut("LEAVE", Keyboard.KEY_ESCAPE, false, false, false, true);
    }

    fun decipherText()
    {
        textPanel.clear()
        options.clearOptions()

        textPanel.addPara("You make your way towards the Terminal found within ${dialog.interactionTarget.name}. It is surrounded by multiple scientists from your fleet.")

        textPanel.addPara("They managed to decipher a variety of words through it, however, those words mostly represent the basic sentence structure of the alien language.\n" +
                "To learn more of it, we need to recover samples of their Language within Rifts, we are likely to find more of it on some planets.", Misc.getHighlightColor(),
            "variety, basic, planets")

        if (!Global.getSector().memoryWithoutUpdate.getBoolean("\$rifts_languageBasicWords"))
        {
            Global.getSector().memoryWithoutUpdate.set("\$rifts_languageBasicWords", true)
            textPanel.addPara("> You aqquired a basic understanding of the language", Misc.getHighlightColor())
        }

        var redacter = WordRedacter()
        var words = Global.getSector().memoryWithoutUpdate.get("\$rifts_totalUnknownWords") as MutableList<String>

        textPanel.addPara(redacter.replace(words.toString()), Misc.getBasePlayerColor())

        var cost: ResourceCostPanelAPI = textPanel.addCostPanel("Cost", 67f, Misc.getBasePlayerColor(), Color.DARK_GRAY)
        var cargo = Global.getSector().playerFleet.cargo

        val required: Int = 1
        val available: Int = cargo.getCommodityQuantity("language_sample").toInt()
        var curr: Color? = Misc.getPositiveHighlightColor()
        var metConditions = true

        if (required > cargo.getQuantity(CargoItemType.RESOURCES, "language_sample")) {
            curr = Misc.getNegativeHighlightColor();
            metConditions = false
        }

        cost.setNumberOnlyMode(true);
        cost.setWithBorder(false);
        cost.setAlignment(Alignment.LMID);

        cost.addCost("language_sample", "" + required + " (" + available + " available)", curr);
        cost.update()

        options.addOption("Learn a new Word", "LEARN_WORD")
        if (!metConditions || Global.getSector().memoryWithoutUpdate.get("\$rifts_learnedAllWords") != null) options.setEnabled("LEARN_WORD", false )

        options.addOption("Back", "MAIN");
        options.setShortcut("MAIN", Keyboard.KEY_ESCAPE, false, false, false, true);
    }

    fun restoreShipsFromHangar()
    {
        options.clearOptions()
        textPanel.clear()
        var derelictFleet = Global.getSector().memoryWithoutUpdate.get("\$rifts_arkship_hangar") as CampaignFleetAPI
        visual.showFleetInfo("Chiral Ships", derelictFleet, null, null)

        textPanel.addPara("You enter the Hangar of ${dialog.interactionTarget.name}. \n\n" +
                "Your engineers greet you, bringing news about the conditions of the derelicts within the hangar.\n" +
                "According to them, many of the stored hulls are broken, but not in an unfixable condition. With enough materials provided, we could restore some of them.",
            Misc.getHighlightColor(), "derelicts", "not in an unfixable condition", "restore")

        var numOfShips: MutableMap<String, Int> = HashMap()
        numOfShips.put("rifts_dune", 0)
        numOfShips.put("rifts_opera", 0)
        numOfShips.put("rifts_phenix", 0)

        for (ship in derelictFleet.fleetData.membersListCopy)
        {
            numOfShips.put(ship.hullSpec.hullId, numOfShips.get(ship.hullSpec.hullId)!!.toInt() + 1)
        }

        var alreadyListed: MutableList<String> = ArrayList()
        for (ship in derelictFleet.fleetData.membersListCopy)
        {
            if (alreadyListed.contains(ship.hullSpec.hullId)) continue

            var tooltip = textPanel.beginTooltip()


            var cargo = Global.getSector().playerFleet.cargo
            if (ship.hullSpec.hullId == "rifts_dune")
            {
                var image = tooltip.beginImageWithText(ship.hullSpec.spriteName, 64f)
                image.addPara("Hull: ${ship.hullSpec.hullName}-class | Size: ${ship.hullSpec.hullSize.toString().lowercase()} | Hulls: ${numOfShips.get(ship.hullSpec.hullId)}", 3f, Misc.getHighlightColor(), "Hull:", "Size:", "Hulls:")
                var repairCost = 30
                var highlightColor = Misc.getPositiveHighlightColor()

                options.addOption("Restore ${ship.hullSpec.hullName}-Class", ship)

                if (repairCost > cargo.getQuantity(CargoItemType.RESOURCES, "exotic_construction"))
                {
                    highlightColor = Misc.getNegativeHighlightColor()
                    options.setEnabled(ship, false)
                    options.setTooltip(ship, "Not Enough Materials available to restore the ship.")
                }

                image.addPara("A small and super fast Frigate", 3f)
                image.addPara("Repair Cost: $repairCost Exotic Construction Components", 3f, highlightColor, "$repairCost")
            }
            if (ship.hullSpec.hullId == "rifts_opera")
            {
                var image = tooltip.beginImageWithText(ship.hullSpec.spriteName, 64f)
                image.addPara("Hull: ${ship.hullSpec.hullName}-class | Size: ${ship.hullSpec.hullSize.toString().lowercase()} | Hulls: ${numOfShips.get(ship.hullSpec.hullId)}", 3f, Misc.getHighlightColor(), "Hull:", "Size:", "Hulls:")

                var repairCost = 50
                var highlightColor = Misc.getPositiveHighlightColor()

                options.addOption("Restore ${ship.hullSpec.hullName}-Class", ship)

                if (repairCost > cargo.getQuantity(CargoItemType.RESOURCES, "exotic_construction"))
                {
                    highlightColor = Misc.getNegativeHighlightColor()
                    options.setEnabled(ship, false)
                    options.setTooltip(ship, "Not Enough Materials available to restore the ship.")
                }

                image.addPara("A heavy Frigate", 3f)
                image.addPara("Repair Cost: $repairCost Exotic Construction Components", 3f, highlightColor, "$repairCost")
            }
            if (ship.hullSpec.hullId == "rifts_phenix")
            {
                var image = tooltip.beginImageWithText(ship.hullSpec.spriteName, 128f)
                image.addPara("Hull: ${ship.hullSpec.hullName}-class | Size: ${ship.hullSpec.hullSize.toString().lowercase()} | Hulls: ${numOfShips.get(ship.hullSpec.hullId)}", 3f, Misc.getHighlightColor(), "Hull:", "Size:", "Hulls:")

                var repairCost = 100
                var highlightColor = Misc.getPositiveHighlightColor()

                options.addOption("Restore ${ship.hullSpec.hullName}-Class", ship)

                if (repairCost > cargo.getQuantity(CargoItemType.RESOURCES, "exotic_construction"))
                {
                    highlightColor = Misc.getNegativeHighlightColor()
                    options.setEnabled(ship, false)
                    options.setTooltip(ship, "Not Enough Materials available to restore the ship.")
                }

                image.addPara("A destroyer that uses its built-in cannon to supress the enemy.", 3f)
                image.addPara("Repair Cost: $repairCost Exotic Construction Components", 3f, highlightColor, "$repairCost")
            }


            tooltip.addImageWithText(1f)
            textPanel.addTooltip()

            alreadyListed.add(ship.hullSpec.hullId)
        }

        options.addOption("Back", "MAIN");
        options.setShortcut("MAIN", Keyboard.KEY_ESCAPE, false, false, false, true);
    }

    override fun optionSelected(optionText: String?, optionData: Any?) {

        if (optionData is FleetMemberAPI)
        {
            var memory = Global.getSector().memoryWithoutUpdate
            var playerfleet = Global.getSector().playerFleet
            playerfleet.fleetData.addFleetMember(optionData)

            var derelictFleet = memory.get("\$rifts_arkship_hangar") as CampaignFleetAPI
            derelictFleet.fleetData.removeFleetMember(optionData)
            memory.set("\$rifts_arkship_hangar", derelictFleet)

            if (optionData.hullSpec.hullId == "rifts_dune") playerfleet.cargo.removeCommodity(RiftStrings.exoticConstructionID, 30f)
            if (optionData.hullSpec.hullId == "rifts_opera") playerfleet.cargo.removeCommodity(RiftStrings.exoticConstructionID, 50f)
            if (optionData.hullSpec.hullId == "rifts_phenix") playerfleet.cargo.removeCommodity(RiftStrings.exoticConstructionID, 100f)

            restoreShipsFromHangar()
        }

        if (optionData !is String) return
        val optionId = optionData as String

        //Rename Window
        if (optionId.contains("RENAME_"))
        {
            options.clearOptions()

            options.addOption("The $optionText", "RENAMEDONE1")
            options.addOption("the $optionText", "RENAMEDONE2")
            options.addOption("${Global.getSector().playerPerson.nameString}'s $optionText", "RENAMEDONE3")
            if (Global.getSector().playerFaction.displayName != "player")
            {
                options.addOption("${Global.getSector().playerFaction.displayName}'s $optionText", "RENAMEDONE4")
            }
        }

        if (optionId.contains("RENAMEDONE"))
        {
            options.clearOptions()

            dialog.interactionTarget.name = optionText
            dialog.interactionTarget.market.name = optionText

            init(dialog)
        }

        //Name Window
        if (optionId.contains("CHOOSE_NAME"))
        {
            options.clearOptions()
            textPanel.addPara("what about the Prefix?", Misc.getHighlightColor(), "$optionText")

            options.addOption("The $optionText", "NAME_CHOOSEN1")
            options.addOption("the $optionText", "NAME_CHOOSEN2")
            options.addOption("${Global.getSector().playerPerson.nameString}'s $optionText", "NAME_CHOOSEN3")
            if (Global.getSector().playerFaction.displayName != "player")
            {
                options.addOption("${Global.getSector().playerFaction.displayName}'s $optionText", "NAME_CHOOSEN4")
            }
        }
        if (optionId.contains("NAME_CHOOSEN"))
        {
            options.clearOptions()
            textPanel.addPara("$optionText it is then.\n" +
                    "As we were finishing up on naming the ship, engineering reported on their analysis on the now active $optionText.\n" +
                    "They managed to find a few broken hulls, resembling those of the ships we encountered so far within the rifts inside of $optionText's Hangar. They arent functional, but we may be able to repair them. \n" +
                    "\nAdditionaly, they disovered a Terminal which seems to hold some kind of writing, but the language is completly foreign. But, we may be able to use it decipher some of it", Misc.getHighlightColor(),
                "$optionText", "broken hulls", "$optionText", "repair", "$optionText", "decipher")
            dialog.interactionTarget.name = optionText
            dialog.interactionTarget.market.name = optionText
            dialog.interactionTarget.customDescriptionId = "arkship"

            options.addOption("Leave", "MAIN");
            options.setShortcut("MAIN", Keyboard.KEY_ESCAPE, false, false, false, true);
        }

        when (optionId)
        {
            "CARGO" ->
            {
                dialog.visualPanel.showCore(CoreUITabId.CARGO, dialog.interactionTarget, null);
            }

            "FLEET" ->
            {
                dialog.visualPanel.showCore(CoreUITabId.FLEET, dialog.interactionTarget, null);
            }

            "REFIT" ->
            {
                dialog.visualPanel.showCore(CoreUITabId.REFIT, dialog.interactionTarget, null);
            }

            "BRING_ONLINE" ->
            {
                Global.getSector().playerFleet.cargo.removeCommodity("strange_matter", 50f)

                options.clearOptions()
                textPanel.clear()

                textPanel.addPara("With the strange matter supplied, we were able to re-ignite this constructions generator.\n" +
                        "\n" +
                        "Suddenly, the fleets instruments start screaming wildy, a dangerous increase in spacetime distortion is starting to emerge from the the construct, and then..." )

                options.addOption("???", "FIRST_WARP");
            }

            "FIRST_WARP" ->
            {
                DismissDialog().execute(null, dialog, null, memoryMap)
                Global.getSector().addScript(ArkshipWarp(dialog.interactionTarget, false))
            }
            "AFTER_FIRST_WARP" ->
            {
                options.clearOptions()
                textPanel.clear()
                textPanel.addPara("We went and installed a Communication device in to the station, aslong as we keep some crew arround, we should be able to start the device remotely.\n" +
                        "We also need to make sure to store some of that strange matter within the cargo holds, otherwise the reactor wont have enough power to create more wormholes.", Misc.getHighlightColor(), "strange matter")
                textPanel.addPara("> Gained \"Wormhole\" Ability", Misc.getPositiveHighlightColor(), "> Gained \"Wormhole\" Ability")

                textPanel.addPara("Now, calling this place by random names is getting tiring, as a reward for our discovery, i think we should be the ones to name it.")
                textPanel.addPara("What should we name it?", Misc.getHighlightColor())

                val abilityId = "arkship_wormhole"
                Global.getSector().characterData.addAbility(abilityId)
                Global.getSector().characterData.memoryWithoutUpdate["\$ability:$abilityId", true] = 0f

                options.addOption("Arkship", "CHOOSE_NAME1")
                options.addOption("Starfarer", "CHOOSE_NAME2")
                options.addOption("Vagabond", "CHOOSE_NAME3")
                options.addOption("Pioneer", "CHOOSE_NAME4")
                options.addOption("Voyager", "CHOOSE_NAME5")
            }
            "RENAME" ->
            {
                options.clearOptions()
                options.addOption("Arkship", "RENAME_1")
                options.addOption("Starfarer", "RENAME_2")
                options.addOption("Vagabond", "RENAME_3")
                options.addOption("Pioneer", "RENAME_4")
                options.addOption("Voyager", "RENAME_5")
                options.addOption(customName, "RENAME_6")
                options.setTooltip("RENAME_6", "To add your own Custom Ship name, go to Mods/Rifts/Data/Config/modSettings.json, open the file and replace \"Custom Name\" with your own name.")
                options.addOption("Back", "MAIN");
                options.setShortcut("MAIN", Keyboard.KEY_ESCAPE, false, false, false, true);
            }

            "HANGAR" ->
            {
                restoreShipsFromHangar()
            }

            "DECIPHER" ->
            {
                decipherText()
            }

            "LEARN_WORD" ->
            {
                var unknownWords = Global.getSector().memoryWithoutUpdate.get("\$rifts_unknownWords") as List<String>
                var newUnknownWords: MutableList<String> = ArrayList()

                Global.getSector().playerFleet.cargo.removeCommodity("language_sample", 1f)
                var learnedWord = unknownWords.get(MathUtils.getRandomNumberInRange(0, unknownWords.size - 1))
                for (word in unknownWords)
                {
                    if (word != learnedWord)
                    {
                        newUnknownWords.add(word)
                    }
                }
                Global.getSector().memoryWithoutUpdate.set("\$rifts_unknownWords", newUnknownWords)
                decipherText()
                if (!unknownWords.isEmpty())
                {
                    textPanel.addPara("> Learned the word for \"$learnedWord\"", Misc.getPositiveHighlightColor())
                }

                if (newUnknownWords.isEmpty())
                {
                    textPanel.clear()
                    options.clearOptions()
                    Global.getSector().memoryWithoutUpdate.set("\$rifts_learnedAllWords", true)

                    decipherText()
                    textPanel.addPara("> Learned the word for \"$learnedWord\"", Misc.getPositiveHighlightColor())
                    textPanel.addPara(">>> You learned all words of the Alien Language", Misc.getPositiveHighlightColor())

                }
            }

            "MAIN" ->
            {
                init(dialog)
            }

            "LEAVE" ->
            {
                DismissDialog().execute(null, dialog, null, memoryMap)
            }


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

    override fun getMemoryMap(): MutableMap<String, MemoryAPI> {
        return memoryMap1
    }


}