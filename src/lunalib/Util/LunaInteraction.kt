package lunalib.Util

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.InteractionDialogAPI
import com.fs.starfarer.api.campaign.InteractionDialogPlugin
import com.fs.starfarer.api.campaign.OptionPanelAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.TextPanelAPI
import com.fs.starfarer.api.campaign.VisualPanelAPI
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.combat.BattleCreationContext
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.fleet.FleetGoal
import com.fs.starfarer.api.impl.campaign.rulecmd.DismissDialog
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

    lateinit var dialog: InteractionDialogAPI
    lateinit var textPanel: TextPanelAPI
    lateinit var optionPanel: OptionPanelAPI
    lateinit var visualPanel: VisualPanelAPI
    lateinit var interactionTarget: SectorEntityToken
    lateinit var targetMemory: MemoryAPI
    lateinit var memory: MemoryAPI

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

    final fun addPara(text: String, highlights: String) : LabelAPI
    {
        return textPanel.addPara(text, Misc.getBasePlayerColor(), Misc.getHighlightColor(), highlights)
    }

    final fun addPara(text: String, baseColor: Color = Misc.getBasePlayerColor(), highlightColor: Color = Misc.getHighlightColor(), highlights: String) : LabelAPI
    {
        return textPanel.addPara(text, baseColor, highlightColor, highlights)
    }

    final fun addOption(name: String, data: Any)
    {
        optionPanel.addOption(name, data)
    }

    override fun optionMousedOver(optionText: String?, optionData: Any?) {

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

