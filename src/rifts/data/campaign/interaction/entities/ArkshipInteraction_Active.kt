package rifts.data.campaign.interaction.entities

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.combat.ShipAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.util.Misc
import lunalib.extension.LunaInteraction
import lunalib.extension.oCallback
import org.lazywizard.lazylib.MathUtils
import org.lwjgl.input.Keyboard
import rifts.data.scripts.ArkshipWarp
import rifts.data.util.RiftData
import rifts.data.util.WordRedacter
import java.awt.Color
import java.util.zip.Inflater

class ArkshipInteraction_Active : LunaInteraction()
{
    override fun mainPage()
    {

        addPara("The fleet comes to a stop at ${RiftData.arkshipPrefix.lowercase()} ${RiftData.arkshipName} , crew can be seen preparing the reactor for its next use." +
                "While we are here, we can make use of the ships Logistical Compartments, or make use of its other features.")

        addOption("Store Cargo in ${dialog.interactionTarget.name}", "CARGO")
        addOption("Store Ships in ${dialog.interactionTarget.name}", "FLEET")
        addOption("Refit Ships in your Fleet", "REFIT")
        optionPanel.setShortcut("CARGO", Keyboard.KEY_I, false, false, false, true);
        optionPanel.setShortcut("FLEET", Keyboard.KEY_F, false, false, false, true);
        optionPanel.setShortcut("REFIT", Keyboard.KEY_R, false, false, false, true);

        addOption("Restore Hulls", oCallback { restoreShips() })
        addOption("Decipher Language", oCallback { decipher() })

        addLeaveOption()
    }

    override fun optionSelected(optionText: String?, optionData: Any?) {
        super.optionSelected(optionText, optionData)

        when (optionData)
        {
            "CARGO" -> dialog.visualPanel.showCore(CoreUITabId.CARGO, dialog.interactionTarget, null);
            "FLEET" -> dialog.visualPanel.showCore(CoreUITabId.FLEET, dialog.interactionTarget, null);
            "REFIT" -> dialog.visualPanel.showCore(CoreUITabId.REFIT, dialog.interactionTarget, null);

            "LEARN_WORD" ->
            {
                var unknownWords = memory.get("\$rifts_unknownWords") as List<String>
                var newUnknownWords: MutableList<String> = ArrayList()

                playerFleet.cargo.removeCommodity(RiftData.languageSampleID, 1f)
                var learnedWord = unknownWords.get(MathUtils.getRandomNumberInRange(0, unknownWords.size - 1))
                for (word in unknownWords)
                {
                    if (word != learnedWord)
                    {
                        newUnknownWords.add(word)
                    }
                }
                memory.set("\$rifts_unknownWords", newUnknownWords)
                decipher()
                if (!unknownWords.isEmpty())
                {
                    addPara("> Learned the word for \"$learnedWord\"", Misc.getPositiveHighlightColor())
                }

                if (newUnknownWords.isEmpty())
                {
                    clear()
                    memory.set("\$rifts_learnedAllWords", true)

                    decipher()
                    addPara("> Learned the word for \"$learnedWord\"", Misc.getPositiveHighlightColor())
                    addPara(">>> You learned all words of the Alien Language", Misc.getPositiveHighlightColor())
                }
            }
        }
    }



    fun decipher()
    {
        clear()

        addPara("You make your way towards the Terminal found within ${dialog.interactionTarget.name}. It is surrounded by multiple scientists from your fleet.")

        addPara("They managed to decipher a variety of words through it, however, those words mostly represent the basic sentence structure of the alien language.\n" +
                "To learn more of it, we need to recover samples of their Language within Rifts, we are likely to find more of it on some planets.", Misc.getHighlightColor(),
            "variety, basic, planets")

        if (!memory.getBoolean("\$rifts_languageBasicWords"))
        {
            memory.set("\$rifts_languageBasicWords", true)
            addPara("> You aqquired a basic understanding of the language", Misc.getHighlightColor())
        }

        var redacter = WordRedacter()
        var words = memory.get("\$rifts_totalUnknownWords") as MutableList<String>

        addPara(redacter.replace(words.toString()), Misc.getBasePlayerColor())

        var metConditions = createCostPanel(RiftData.languageSampleID, 1, true)

        addOption("Learn a new Word", "LEARN_WORD")
        if (!metConditions || memory.get("\$rifts_learnedAllWords") != null) optionPanel.setEnabled("LEARN_WORD", false)


        addBackOption()
    }


    fun restoreShips()
    {
        clear()

        var derelictFleet = memory.get("\$rifts_arkship_hangar") as CampaignFleetAPI
        visualPanel.showFleetInfo("Chiral Ships", derelictFleet, null, null)

        textPanel.addPara("You enter the Hangar of ${RiftData.arkshipName}. \n\n" +
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

            var repairCost = 0
            var description = ""
            var imageSize = 64f
            var cargo = playerFleet.cargo

            when (ship.hullSpec.hullId)
            {
                "rifts_dune" -> { repairCost = 30; description = "A small and super fast Frigate" }
                "rifts_opera" -> { repairCost = 50; description = "A heavy Frigate" }
                "rifts_phenix" -> { repairCost = 100; description = "A destroyer that uses its built-in cannon to supress the enemy."; imageSize = 128f }
            }

            var tooltip = textPanel.beginTooltip()
            var image = tooltip.beginImageWithText(ship.hullSpec.spriteName, imageSize)
            image.addPara("Hull: ${ship.hullSpec.hullName}-class | Size: ${ship.hullSpec.hullSize.toString().lowercase()} | Hulls: ${numOfShips.get(ship.hullSpec.hullId)}", 3f, Misc.getHighlightColor(), "Hull:", "Size:", "Hulls:")
            var highlightColor = Misc.getPositiveHighlightColor()

            if (repairCost > cargo.getQuantity(CargoAPI.CargoItemType.RESOURCES, "exotic_construction"))
            {
                highlightColor = Misc.getNegativeHighlightColor()
                addOption("Restore ${ship.hullSpec.hullName}", "PLACEHOLDER ${ship.hullSpec.hullId}")

                setEnabled("PLACEHOLDER ${ship.hullSpec.hullId}", false)
                optionPanel.setTooltip("PLACEHOLDER ${ship.hullSpec.hullId}", "Not Enough Materials available to restore the ship.")
            }
            else
            {
                addOption("Restore ${ship.hullSpec.hullName}-Class", oCallback { shipRepaired(ship, repairCost) })
            }


            image.addPara("A small and super fast Frigate", 3f)
            image.addPara("Repair Cost: $repairCost Exotic Construction Components", 3f, highlightColor, "$repairCost")

            tooltip.addImageWithText(0f)
            textPanel.addTooltip()

            alreadyListed.add(ship.hullSpec.hullId)
        }
        addBackOption()
    }

    fun shipRepaired(ship: FleetMemberAPI, cost: Int)
    {
        playerFleet.fleetData.addFleetMember(ship)

        var derelictFleet = memory.get("\$rifts_arkship_hangar") as CampaignFleetAPI
        derelictFleet.fleetData.removeFleetMember(ship)
        memory.set("\$rifts_arkship_hangar", derelictFleet)

        playerFleet.cargo.removeCommodity(RiftData.exoticConstructionID, cost.toFloat())

        restoreShips()
    }
}