package rifts.data.campaign.intel

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin
import com.fs.starfarer.api.ui.Alignment
import com.fs.starfarer.api.ui.IntelUIAPI
import com.fs.starfarer.api.ui.SectorMapAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import rifts.data.campaign.interaction.planets.PlanetRuins_Clue1
import rifts.data.campaign.interaction.planets.PlanetRuins_Clue2
import rifts.data.util.RiftRuinsData
import rifts.data.util.WordRedacter
import java.awt.Color


class RiftsLogIntel() : BaseIntelPlugin()
{
    private var remove = false

    init
    {

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
        return "Rifts Log"
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

        info.addPara("A log containing data aqquired within Rifts.", 10f)


        var count = 0
        if (!Global.getSector().getEntityById("ArkshipPlanet").hasTag(RiftRuinsData.cluePlanet1Tag)) count++
        if (!Global.getSector().getEntityById("OriginStarPlanet").hasTag(RiftRuinsData.cluePlanet2Tag))  count++

        if (count != 0) info.addSectionHeading("Writing found within Ruins ($count / 2)", Alignment.MID, 10f)

        if (!Global.getSector().getEntityById("ArkshipPlanet").hasTag(RiftRuinsData.cluePlanet1Tag))
        {
            var redactor = WordRedacter()

            var text: String = redactor.replace(PlanetRuins_Clue1.Clue1Text)

            var label = info.addPara(text, 10f, Misc.getBasePlayerColor())
        }

        if (!Global.getSector().getEntityById("OriginStarPlanet").hasTag(RiftRuinsData.cluePlanet2Tag))
        {
            var redactor = WordRedacter()

            var text: String = redactor.replace(PlanetRuins_Clue2.Clue2Text)

            var label = info.addPara(text, 10f, Misc.getBasePlayerColor())
        }
    }

    override fun doesButtonHaveConfirmDialog(buttonId: Any?): Boolean {
        return false
    }

    override fun buttonPressConfirmed(buttonId: Any, ui: IntelUIAPI)
    {

    }

    override fun getIcon(): String? {
        return Global.getSettings().getSpriteName("intel", "important")
    }

    override fun getIntelTags(map: SectorMapAPI?): Set<String>? {
        val tags = super.getIntelTags(map)
        tags.add("Rifts")
        return tags
    }

    override fun getCommMessageSound(): String? {
        return super.getCommMessageSound()
    }
}