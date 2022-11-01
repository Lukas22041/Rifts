package rifts.data.campaign.interaction

import com.fs.starfarer.api.campaign.*
import com.fs.starfarer.api.campaign.rules.MemKeys
import com.fs.starfarer.api.campaign.rules.MemoryAPI
import com.fs.starfarer.api.combat.EngagementResultAPI
import com.fs.starfarer.api.impl.campaign.rulecmd.DismissDialog
import org.lwjgl.input.Keyboard
import rifts.data.util.WordRedacter

class OriginInteraction(phase: String) : InteractionDialogPlugin {


    var phase: String
    lateinit var dialog: InteractionDialogAPI
    private var memoryMap1: MutableMap<String, MemoryAPI> = HashMap()
    lateinit var textPanel: TextPanelAPI
    lateinit var options: OptionPanelAPI
    lateinit var visual: VisualPanelAPI

    init {
        this.phase = phase
    }

    override fun init(dialog: InteractionDialogAPI?)
    {

        if (dialog == null) return
        this.dialog = dialog

        textPanel = dialog.textPanel
        visual = dialog.visualPanel
        options = dialog.optionPanel

        memoryMap1.clear()
        var memory = dialog.interactionTarget.memory
        memoryMap1.put(MemKeys.LOCAL, memory);

        if (phase == "start")
        {
            textPanel.addPara("We arrive in a system, containing nothing but a single star.\n\n" +
                    "Strangely, the star has not been marked on any hyperspace map. Upon closer inspection, it emitts strange radio waves, some of which our instruments cant make sense of.\n" +
                    "\nAs we fly closer to inspect, we mysteriously started to close in on the star faster and faster...")
        }
        var redacter = WordRedacter()

        options.addOption("Continue", "LEAVE");
        options.setShortcut("LEAVE", Keyboard.KEY_ESCAPE, false, false, false, true);

    }

    override fun optionSelected(optionText: String?, optionData: Any?)
    {
        val optionId = optionData as String

        when (optionId)
        {
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