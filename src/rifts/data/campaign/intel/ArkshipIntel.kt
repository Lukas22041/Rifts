package rifts.data.campaign.intel

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin
import com.fs.starfarer.api.impl.campaign.ids.Submarkets
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.IntelUIAPI
import com.fs.starfarer.api.ui.SectorMapAPI
import com.fs.starfarer.api.ui.TextFieldAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import rifts.data.util.RiftData
import java.awt.Color


class ArkshipIntel() : BaseIntelPlugin()
{
    private var remove = false

    companion object
    {
        var nameField: TextFieldAPI? = null
        var prefixField: TextFieldAPI? = null
    }

    override fun reportPlayerClickedOn() {
        super.reportPlayerClickedOn()
    }

    override fun reportRemovedIntel() {
        super.reportRemovedIntel()
        Global.getSector().removeScript(this)
    }


    override fun shouldRemoveIntel(): Boolean {
        return remove
    }

    override fun getSmallDescriptionTitle(): String {
        return "${RiftData.arkshipPrefix} ${RiftData.arkshipName} "
    }

    override fun createIntelInfo(info: TooltipMakerAPI, mode: IntelInfoPlugin.ListInfoMode?) {
        val c = getTitleColor(mode)
        info.addPara(smallDescriptionTitle, c, 1f)
    }

    override fun createSmallDescription(info: TooltipMakerAPI, width: Float, height: Float) {
        val tc = Misc.getTextColor()
        val UIColor: Color = Global.getSector().playerFaction.baseUIColor
        val UIColorDark: Color = Global.getSector().playerFaction.darkUIColor
        val mainColor = Global.getSector().playerFaction.baseUIColor
        val highlightColor = Misc.getHighlightColor()

        val Arkship: SectorEntityToken = Global.getSector().getEntityById("Arkship") ?: return

        val system = Arkship.starSystem
        var systemInfo = "It currently resides in the ${system.name}."
        if (system.hasTag(RiftData.DimensionalRift)) systemInfo = "It currently resides in a dimensional Rift."


        info.addSpacer(5f)
        info.addPara("A collection of communication data from the Arkship. $systemInfo", 0f)
        info.addSpacer(5f)


        info.addSectionHeading("Fuel", Alignment.MID, 0f)

        info.addSpacer(5f)
        var storageSubmarket = Arkship.market.getSubmarket(Submarkets.SUBMARKET_STORAGE).plugin as StoragePlugin

        var storedStrangeMatter = storageSubmarket.cargo.getCommodityQuantity(RiftData.strangeMatterID)
        info.addPara("The ship currently holds $storedStrangeMatter units of strange matter within its storage holds.", 0f,Misc.getHighlightColor(), "$storedStrangeMatter")
        info.addSpacer(5f)


        info.addSectionHeading("Ship Name", Alignment.MID, 0f)
        info.addSpacer(5f)
        info.addPara("Change the ships name by entering a new name and prefix in to the Fields, then pressing the \"Save Name\" button.", 0f)
        info.addSpacer(5f)

        prefixField = info.addTextField(width, 0f)
        prefixField!!.text = RiftData.arkshipPrefix

        nameField = info.addTextField(width, 0f)
        nameField!!.text = RiftData.arkshipName


        info.addButton("Save Name", "SAVE", UIColor, UIColorDark, width, 20f, 10f * 2f);
    }

    override fun doesButtonHaveConfirmDialog(buttonId: Any?): Boolean {
        return false
    }

    override fun buttonPressConfirmed(buttonId: Any, ui: IntelUIAPI)
    {
        if (buttonId == "SAVE")
        {
            if (prefixField == null || nameField == null) return

            RiftData.arkshipName = nameField!!.text
            RiftData.arkshipPrefix = prefixField!!.text

            Global.getSector().getEntityById("Arkship").name = "${RiftData.arkshipPrefix} ${RiftData.arkshipName}"
            Global.getSector().getEntityById("Arkship").market.name = "${RiftData.arkshipPrefix} ${RiftData.arkshipName}"
            ui.recreateIntelUI()
        }
    }

    override fun getIcon(): String? {
        return Global.getSettings().getSpriteName("intel", "tradeFleet_large")
    }

    override fun getIntelTags(map: SectorMapAPI?): Set<String>? {
        val tags = super.getIntelTags(map)
        tags.add("Rifts")
        return tags
    }

    override fun getCommMessageSound(): String? {
        return super.getCommMessageSound()
    }

    override fun getMapLocation(map: SectorMapAPI?): SectorEntityToken {
        return Global.getSector().getEntityById("Arkship")
    }
}