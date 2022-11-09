package rifts.data.campaign

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.PluginPick
import com.fs.starfarer.api.campaign.BaseCampaignPlugin
import com.fs.starfarer.api.campaign.CampaignPlugin
import com.fs.starfarer.api.campaign.InteractionDialogPlugin
import com.fs.starfarer.api.campaign.JumpPointAPI.JumpDestination
import com.fs.starfarer.api.campaign.SectorEntityToken
import rifts.data.campaign.interaction.entities.ArkshipInteraction_Active
import rifts.data.campaign.interaction.entities.ArkshipInteraction_FirstWarp
import rifts.data.campaign.interaction.entities.ArkshipInteraction_Inactive
import rifts.data.campaign.interaction.entities.FirstWormholeInteraction
import rifts.data.campaign.interaction.planets.PlanetRuins_Clue1
import rifts.data.campaign.interaction.planets.PlanetRuins_Clue2
import rifts.data.campaign.interaction.planets.PlanetRuins_Common
import rifts.data.campaign.interaction.planets.PlanetRuins_NoRuins
import rifts.data.util.RiftRuinsData
import rifts.data.util.RiftData

class RiftsCampaignPlugin : BaseCampaignPlugin()
{

    override fun isTransient(): Boolean {
        return true
    }

    override fun pickInteractionDialogPlugin(interactionTarget: SectorEntityToken?): PluginPick<InteractionDialogPlugin>? {
        if (interactionTarget == null) return null

        if (interactionTarget.hasTag(RiftData.RiftPlanet))
        {
            if (interactionTarget.hasTag(RiftRuinsData.cluePlanet1Tag)) return PluginPick<InteractionDialogPlugin>(PlanetRuins_Clue1(), CampaignPlugin.PickPriority.HIGHEST)
            else if (interactionTarget.hasTag(RiftRuinsData.cluePlanet2Tag)) return PluginPick<InteractionDialogPlugin>(PlanetRuins_Clue2(), CampaignPlugin.PickPriority.HIGHEST)
            else if (interactionTarget.hasTag(RiftRuinsData.commonRuinsTag)) return PluginPick<InteractionDialogPlugin>(PlanetRuins_Common(), CampaignPlugin.PickPriority.HIGHEST)
            else return PluginPick<InteractionDialogPlugin>(PlanetRuins_NoRuins(), CampaignPlugin.PickPriority.HIGHEST)
        }

        if (interactionTarget.id.contains("Rift_Wormhole"))
        {
            if (Global.getSector().memoryWithoutUpdate.get("\$RiftHasBeenUsed") == null)
            {
                return PluginPick<InteractionDialogPlugin>(FirstWormholeInteraction(), CampaignPlugin.PickPriority.HIGHEST)
            }
            else
            {
                Global.getSector().doHyperspaceTransition(Global.getSector().playerFleet, interactionTarget, JumpDestination(interactionTarget.memoryWithoutUpdate.get("\$WormholeDestination") as SectorEntityToken, "debug") , 0.1f);
            }
        }

        if (interactionTarget.id == "Arkship")
        {
            if (interactionTarget.hasTag(RiftData.ArkshipInactive)) return PluginPick<InteractionDialogPlugin>(ArkshipInteraction_Inactive(), CampaignPlugin.PickPriority.HIGHEST)
            else if (interactionTarget.hasTag(RiftData.ArkshipWarped)) return PluginPick<InteractionDialogPlugin>(ArkshipInteraction_FirstWarp(), CampaignPlugin.PickPriority.HIGHEST)
            else return PluginPick<InteractionDialogPlugin>(ArkshipInteraction_Active(), CampaignPlugin.PickPriority.HIGHEST)
        }

        return null

    }
}