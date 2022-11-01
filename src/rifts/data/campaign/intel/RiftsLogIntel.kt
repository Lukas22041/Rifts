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
        if (Global.getSector().getEntityById("ArkshipPlanet").hasTag(RiftStrings.RecoveredLanguage)) count++
        if (Global.getSector().getEntityById("OriginStarPlanet").hasTag(RiftStrings.RecoveredLanguage)) count++

        if (count != 0) info.addSectionHeading("Writing found within Ruins ($count / 2)", Alignment.MID, 10f)

        if (Global.getSector().getEntityById("ArkshipPlanet").hasTag(RiftStrings.RecoveredLanguage))
        {
            var redactor = WordRedacter()

            var text: String = redactor.replace("\"Origin Stars shine in a great, green light.\n" +
                    "They have some special characteristics, that make them essential to our species. If supplied with a large quantity of strange matter, they create large spacetime distortions, the star seemingly starting to exist in 2 dimensions at once. \n" +
                    "Then, once it goes supernova, it leaves a wormhole at its core. The Entire reaction also causes Universes to close in on eachother, causing wormholes to appear all around space." +
                    " \"")

            var label = info.addPara(text, 10f, Misc.getBasePlayerColor())
        }

        if (Global.getSector().getEntityById("OriginStarPlanet").hasTag(RiftStrings.RecoveredLanguage))
        {
            var redactor = WordRedacter()

            var text: String = redactor.replace("\"However, after some time, Spacetime corrects itself, and closes the wormhole.  That is not to say though that there is no use for them afterwards though.  " +
                    "If you leave some small traces of strange matter in the system of its of its origin, and ignite it using the force of a wormhole traveling craft, it will create enough distortion to reopen the wormhole\"")

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