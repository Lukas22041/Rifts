package rifts.data.campaign.intel

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.JumpPointAPI.JumpDestination
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.IntelUIAPI
import com.fs.starfarer.api.ui.SectorMapAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import rifts.data.campaign.procgen.specs.RiftSpec
import rifts.data.util.RiftStrings
import java.awt.Color


class WormholeIntel(targetSystem: StarSystemAPI) : BaseIntelPlugin()
{
    private var remove = false
    private val targetSystem: StarSystemAPI
    private var riftWormhole: SectorEntityToken? = null

    init
    {
        this.targetSystem = targetSystem

        for (entity in targetSystem.allEntities)
        {
            if (entity.hasTag(RiftStrings.wormholeEntity))
            {
                riftWormhole = entity.memoryWithoutUpdate.get("\$WormholeDestination") as SectorEntityToken
            }
        }
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
        return "${targetSystem.name} Wormhole"
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

        if (riftWormhole!!.starSystem.hasTag(RiftStrings.RiftExplored))
        {
            info.addPara("A wormhole residing in the ${targetSystem.name}.", 3f, mainColor, highlightColor)

            info.addSectionHeading("Explored", Alignment.MID, 3f)
        }
        else
        {
            info.addPara("We detected the signature of a wormhole in the ${targetSystem.name}.", 3f, mainColor, highlightColor)
        }

        if (!Global.getSettings().isDevMode) return

        info.addSectionHeading("Dev Tools", Alignment.MID, 10f)
        info.addButton("Teleport to System", "TELEPORT_TO_SYSTEM", UIColor, UIColorDark, width, 20f, 10f * 2f);
        info.addButton("Teleport to Rift", "TELEPORT_TO_RIFT", UIColor, UIColorDark, width, 20f, 10f * 2f);

        info.addSpacer(10f)

        info.addSectionHeading("System Info", Alignment.MID, 10f)
        var starType: String = "none"
        if (targetSystem.star != null)
        {
            starType = targetSystem.star.typeId
        }
        info.addPara("System Star: $starType", 3f, mainColor, highlightColor, "System Star:")
        info.addPara("System Tags: ${targetSystem.tags}", 3f, mainColor, highlightColor, "System Tags:")

        info.addSectionHeading("Rift Info", Alignment.MID, 10f)

        var riftSpec = riftWormhole!!.starSystem.memoryWithoutUpdate.get(RiftStrings.riftSpecMemoryKey) as RiftSpec

        info.addPara("Rift Type: ${riftSpec.RiftSpecID}", 3f, mainColor, highlightColor, "Rift Type:")

        var riftStarType: String = "none"
        if (riftWormhole!!.starSystem.star != null)
        {
            riftStarType = riftWormhole!!.starSystem.star.typeId
        }

        info.addPara("Star Type: $riftStarType", 3f, mainColor, highlightColor, "Star Type:")
        info.addPara("Rift Background: ${riftWormhole!!.starSystem.backgroundTextureFilename}", 3f, mainColor, highlightColor, "Rift Background:")
        info.addPara("Rift Tags: ${riftWormhole!!.starSystem.tags}", 3f, mainColor, highlightColor, "Rift Tags:")



    }

    override fun doesButtonHaveConfirmDialog(buttonId: Any?): Boolean {
        return false
    }

    override fun buttonPressConfirmed(buttonId: Any, ui: IntelUIAPI) {

        if (buttonId == "TELEPORT_TO_SYSTEM")
        {
            Global.getSector().doHyperspaceTransition(Global.getSector().playerFleet, Global.getSector().playerFleet, JumpDestination(riftWormhole!!.memoryWithoutUpdate.get("\$WormholeDestination") as SectorEntityToken, ""), 1f)
        }
        if (buttonId == "TELEPORT_TO_RIFT")
        {
            Global.getSector().doHyperspaceTransition(Global.getSector().playerFleet, Global.getSector().playerFleet, JumpDestination(riftWormhole, ""), 1f)
        }

        ui.updateUIForItem(this)
    }

    override fun getIcon(): String? {
        return Global.getSettings().getSpriteName("intel", "gate_active")
    }

    override fun getIntelTags(map: SectorMapAPI?): Set<String>? {
        val tags = super.getIntelTags(map)
        tags.add("Rifts")
        return tags
    }

    override fun getMapLocation(map: SectorMapAPI?): SectorEntityToken? {
                return targetSystem.hyperspaceAnchor
    }

    override fun getCommMessageSound(): String? {
        return super.getCommMessageSound()
    }
}