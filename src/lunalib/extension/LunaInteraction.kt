package lunalib.extension

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.combat.BattleCreationContext
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.FIDConfig
import com.fs.starfarer.api.impl.campaign.ids.MemFlags
import com.fs.starfarer.api.impl.campaign.rulecmd.DismissDialog
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.FleetAdvanceScript
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed.SDMParams
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed.SalvageDefenderModificationPlugin
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.util.Misc
import org.lwjgl.input.Keyboard
import java.awt.Color

fun interface oCallback
{
    fun callback()
}

/**
 * Class that inherits from InteractionDialogPlugin and takes care of some of the boilerplate.
 * @property dialog
 * @property textPanel
 * @property optionPanel
 * @property visualPanel
 * @property interactionTarget
 * @property targetMemory
 * @property memory Global Memory
 */
abstract class LunaInteraction() : InteractionDialogPlugin
{
    // General variables that are commonly used
    lateinit var dialog: InteractionDialogAPI
    lateinit var textPanel: TextPanelAPI
    lateinit var optionPanel: OptionPanelAPI
    lateinit var visualPanel: VisualPanelAPI
    lateinit var interactionTarget: SectorEntityToken
    lateinit var targetMemory: MemoryAPI
    lateinit var memory: MemoryAPI

    // Preset Color variables to make accessing those easier
    var playerColor = Misc.getBasePlayerColor()
    var brightPlayerColor = Misc.getBrightPlayerColor()
    var darkPlayercolor = Misc.getDarkPlayerColor()
    var highlightColor = Misc.getHighlightColor()
    var positiveColor = Misc.getPositiveHighlightColor()
    var negativeColor = Misc.getNegativeHighlightColor()
    var textColor = Misc.getTextColor()

    // Some utility variables that wont change during a dialog
    var sector = Global.getSector()
    var playerFleet = Global.getSector().playerFleet

    override fun init(dialog: InteractionDialogAPI) {
        this.dialog = dialog
        this.textPanel = dialog.textPanel
        this.optionPanel = dialog.optionPanel
        this.visualPanel = dialog.visualPanel
        this.interactionTarget = dialog.interactionTarget
        this.targetMemory = dialog.interactionTarget.memoryWithoutUpdate
        this.memory = Global.getSector().memoryWithoutUpdate

        if (dialog.interactionTarget.customInteractionDialogImageVisual != null)
        {
            visualPanel.showImageVisual(dialog.interactionTarget.customInteractionDialogImageVisual)
        }

        mainPage()
    }

    abstract fun mainPage()

    override fun optionSelected(optionText: String?, optionData: Any?)
    {
        if (optionData is String)
        {
            if (optionData == "LEAVE") closeDialog()
            if (optionData == "BACKTOMAIN") reset()
        }
        if (optionData is oCallback)
        {
            optionData.callback()
        }
    }




    //Utility Functions
    final fun reset()
    {
        textPanel.clear()
        optionPanel.clearOptions()
        mainPage()
    }

    final fun clear()
    {
        textPanel.clear()
        optionPanel.clearOptions()
    }

    final fun closeDialog()
    {
        DismissDialog().execute(null, dialog, null, memoryMap)
    }

    final fun addBackOption()
    {
        optionPanel.addOption("Back", "BACKTOMAIN");
        optionPanel.setShortcut("BACKTOMAIN", Keyboard.KEY_ESCAPE, false, false, false, true);
    }

    final fun addLeaveOption()
    {
        optionPanel.addOption("Leave", "LEAVE");
        optionPanel.setShortcut("LEAVE", Keyboard.KEY_ESCAPE, false, false, false, true);
    }

    final fun addPara(text: String) : LabelAPI
    {
        return textPanel.addPara(text)
    }

    final fun addPara(text: String, baseColor: Color) : LabelAPI
    {
        return textPanel.addPara(text, baseColor)
    }

    final fun addPara(text: String, vararg highlights: String) : LabelAPI
    {
        return textPanel.addPara(text, Misc.getBasePlayerColor(), Misc.getHighlightColor(), *highlights)
    }

    final fun addPara(text: String, highlightColor: Color = Misc.getHighlightColor(), vararg highlights: String) : LabelAPI
    {
        return textPanel.addPara(text, highlightColor, *highlights)
    }

    final fun addPara(text: String, baseColor: Color = Misc.getBasePlayerColor(), highlightColor: Color = Misc.getHighlightColor(), vararg highlights: String) : LabelAPI
    {
        return textPanel.addPara(text, baseColor, highlightColor, *highlights)
    }

    final fun addOption(name: String, data: Any)
    {
        optionPanel.addOption(name, data)
    }

    fun setEnabled(optionData: Any, enabled: Boolean)
    {
        optionPanel.setEnabled(optionData, enabled)
    }

    fun createCostPanel(commodity: String, required: Int, border: Boolean = true) : Boolean
    {
        var cost: ResourceCostPanelAPI = textPanel.addCostPanel("Cost", 67f, Misc.getBasePlayerColor(), Color.DARK_GRAY)
        var cargo = Global.getSector().playerFleet.cargo

        val available: Int = cargo.getCommodityQuantity(commodity).toInt()
        var color: Color? = Misc.getPositiveHighlightColor()
        var metConditions = true

        if (required > available) {
            color = Misc.getNegativeHighlightColor();
            metConditions = false
        }

        cost.setNumberOnlyMode(true);
        cost.setWithBorder(false);
        cost.setAlignment(Alignment.LMID);

        cost.addCost(commodity, "" + required + " (" + available + " available)", color);
        cost.update()

        return metConditions
    }

    override fun optionMousedOver(optionText: String?, optionData: Any?) {

    }

    final fun triggerDefenders()
    {
        if (targetMemory.getFleet("\$defenderFleet") == null) return

        val entity = dialog.interactionTarget
        val defenders = targetMemory.getFleet("\$defenderFleet")

        dialog.interactionTarget = defenders

        val config = FIDConfig()
        config.leaveAlwaysAvailable = true
        config.showCommLinkOption = false
        config.showEngageText = false
        config.showFleetAttitude = false
        config.showTransponderStatus = false
        config.showWarningDialogWhenNotHostile = false
        config.alwaysAttackVsAttack = true
        config.impactsAllyReputation = true
        config.impactsEnemyReputation = false
        config.pullInAllies = false
        config.pullInEnemies = false
        config.pullInStations = false
        config.lootCredits = false

        config.firstTimeEngageOptionText = "Engage the chiral defenses"
        config.afterFirstTimeEngageOptionText = "Re-engage the chiral defenses"
        config.noSalvageLeaveOptionText = "Continue"

        config.dismissOnLeave = false
        config.printXPToDialog = true

        val seed = targetMemory.getLong(MemFlags.SALVAGE_SEED)
        config.salvageRandom = Misc.getRandom(seed, 75)

        val plugin = FleetInteractionDialogPluginImpl(config)

        val originalPlugin = dialog.plugin

        config.delegate = FIDOverride(defenders, dialog, plugin, originalPlugin, this)

        dialog.plugin = plugin
        plugin.init(dialog)
    }

    open fun defeatedDefenders()
    {

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







class FIDOverride(defenders: CampaignFleetAPI, dialog: InteractionDialogAPI, plugin: FleetInteractionDialogPluginImpl, originalPlugin: InteractionDialogPlugin, LunaInt: LunaInteraction) : FleetInteractionDialogPluginImpl.BaseFIDDelegate()
{

    var defenders: CampaignFleetAPI = defenders
    var LunInteraction = LunaInt

    var plugin = plugin
    var originalPlugin = originalPlugin

    override fun notifyLeave(dialog: InteractionDialogAPI) {
        // nothing in there we care about keeping; clearing to reduce savefile size
        val entity = dialog.interactionTarget
        defenders.getMemoryWithoutUpdate().clear()
        // there's a "standing down" assignment given after a battle is finished that we don't care about
        defenders.clearAssignments()
        defenders.deflate()
        var memory = dialog.interactionTarget.memoryWithoutUpdate
        dialog.plugin = originalPlugin
        dialog.interactionTarget = entity

        //Global.getSector().getCampaignUI().clearMessages();
        if (plugin.getContext() is FleetEncounterContext) {
            val context = plugin.getContext() as FleetEncounterContext
            if (context.didPlayerWinEncounterOutright()) {
                val p = SDMParams()
                p.entity = entity
                p.factionId = defenders.getFaction().getId()
                val plugin =
                    Global.getSector().genericPlugins.pickPlugin(SalvageDefenderModificationPlugin::class.java, p)
                plugin?.reportDefeated(p, entity, defenders)
                memory.unset("\$hasDefenders")
                memory.unset("\$defenderFleet")
                memory.set("\$defenderFleetDefeated", true)
                entity.removeScriptsOfClass(FleetAdvanceScript::class.java)
                LunInteraction.defeatedDefenders()
            } else {
                var persistDefenders = false
                if (context.isEngagedInHostilities) {
                    persistDefenders = persistDefenders or !Misc.getSnapshotMembersLost(defenders).isEmpty()
                    for (member in defenders.getFleetData().getMembersListCopy()) {
                        if (member.status.needsRepairs()) {
                            persistDefenders = true
                            break
                        }
                    }
                }
                if (persistDefenders) {
                    if (!entity.hasScriptOfClass(FleetAdvanceScript::class.java)) {
                        defenders.setDoNotAdvanceAI(true)
                        defenders.setContainingLocation(entity.getContainingLocation())
                        // somewhere far off where it's not going to be in terrain or whatever
                        defenders.setLocation(1000000f, 1000000f)
                        entity.addScript(FleetAdvanceScript(defenders))
                    }
                    memory.expire("\$defenderFleet", 10f) // defenders may have gotten damaged; persist them for a bit
                }
                dialog.dismiss()
            }
        } else {
            dialog.dismiss()
        }
    }

    override fun battleContextCreated(dialog: InteractionDialogAPI?, bcc: BattleCreationContext) {
        bcc.aiRetreatAllowed = false
        bcc.objectivesAllowed = false
        bcc.enemyDeployAll = true
    }
}

