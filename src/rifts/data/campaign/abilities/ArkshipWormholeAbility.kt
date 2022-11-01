package rifts.data.campaign.abilities

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.impl.campaign.abilities.BaseDurationAbility
import com.fs.starfarer.api.impl.campaign.ids.Submarkets
import com.fs.starfarer.api.impl.campaign.submarkets.StoragePlugin
import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import data.scripts.util.MagicSettings
import lunalib.Util.LunaTooltip
import org.lazywizard.lazylib.MathUtils
import rifts.data.scripts.ArkshipWarp
import rifts.data.scripts.OriginWormhole
import java.awt.Color


class ArkshipWormholeAbility : BaseDurationAbility() {

    var requiredFuel = MagicSettings.getInteger("rifts", "warp_fuel_cost")
    var strangeMatterID = "strange_matter"
    var canWarp = false
    var storedStrangeMatter = 0f
    var arkshipName = ""
    var contextcolor: Color = Color(255, 255, 255, 255)

    override fun activateImpl() {

        if (canWarp)
        {
            //Logic for creating the puzzle wormhole
            var strangeMatterCrate: SectorEntityToken? = null
            var playerfleet = Global.getSector().playerFleet
            if (playerfleet.starSystem.center.isStar)
            {
                if (playerfleet.starSystem.star.hasTag("CanCreateWormhole"))
                {
                    var entitiesInSystem = Global.getSector().playerFleet.starSystem.customEntities
                    for (entity in entitiesInSystem)
                    {
                        if (entity.customEntitySpec.id == "cargo_pods")
                        {
                            var distance = MathUtils.getDistance(playerfleet, entity)
                            if (entity.cargo.getCommodityQuantity(strangeMatterID) >= 1 && distance < 500f)
                            {
                                strangeMatterCrate = entity
                            }
                        }
                    }
                }
            }

            var arkship = Global.getSector().getEntityById("Arkship")
            var storageSubmarket = arkship.market.getSubmarket(Submarkets.SUBMARKET_STORAGE).plugin as StoragePlugin
            storageSubmarket.cargo.removeCommodity(strangeMatterID, requiredFuel.toFloat())

            if (strangeMatterCrate == null)
            {
                Global.getSector().addScript(ArkshipWarp(Global.getSector().getEntityById("Arkship"), true))
            }
            else
            {
                Global.getSector().addScript(OriginWormhole(playerfleet.starSystem, strangeMatterCrate))
                playerfleet.starSystem.star.removeTag("CanCreateWormhole")
            }
        }
        else
        {
            cooldownLeft = 0f
        }
    }

    override fun advance(amount: Float) {
        super.advance(amount)
        if (Global.getSector().getEntityById("Arkship") != null)
        {
            var arkship = Global.getSector().getEntityById("Arkship")
            var storageSubmarket = arkship.market.getSubmarket(Submarkets.SUBMARKET_STORAGE).plugin as StoragePlugin

            storedStrangeMatter = storageSubmarket.cargo.getCommodityQuantity(strangeMatterID)
            arkshipName = arkship.name
        }

        if (requiredFuel > storedStrangeMatter || Global.getSector().playerFleet.isInHyperspace)
        {
            contextcolor = Misc.getNegativeHighlightColor()
            canWarp = false
        }
        else
        {
            contextcolor = Misc.getPositiveHighlightColor()
            canWarp = true
        }
    }

    override fun applyEffect(amount: Float, level: Float) {

    }

    override fun deactivateImpl() {

    }

    override fun cleanupImpl() {

    }

    override fun createTooltip(tip: TooltipMakerAPI, expanded: Boolean)
    {

        val title: LabelAPI = tip.addTitle(spec.name)
        title.setHighlightColor(Misc.getGrayColor())

        var tooltip = LunaTooltip()
        tooltip.refreshKeyword("shipname",arkshipName)
        tooltip.refreshKeyword("strangeMatterHeld", "$storedStrangeMatter")
        tooltip.addColor("contextcolor", contextcolor)
        tooltip.addTextCSV(tip, "arkship_tooltip_base")

        if (Global.getSector().playerFleet.isInHyperspace)
        {
            tip.addPara("${arkshipName} can not be summoned in to Hyperspace", Misc.getNegativeHighlightColor(), 10f)
        }
        else if (requiredFuel > storedStrangeMatter)
        {
            tip.addPara("Not enough strange matter stored to create a wormhole", Misc.getNegativeHighlightColor(), 10f)
        }
     }

    override fun hasTooltip(): Boolean {
        return true
    }
}