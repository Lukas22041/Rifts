package rifts.data.campaign.entity

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.CampaignEngineLayers
import com.fs.starfarer.api.combat.ViewportAPI
import com.fs.starfarer.api.graphics.SpriteAPI
import com.fs.starfarer.api.impl.campaign.BaseCustomEntityPlugin
import java.awt.Color


class WormholeEntity : BaseCustomEntityPlugin()
{

    private val sprite: SpriteAPI?
    private val sprite2: SpriteAPI?

    var ogHeight: Float
    var ogWidth: Float

    private var wormholeAlpha = 200
    private var wormholeSizeMult = 1f
    private var wormholeRotationSpeedMult = 1f
    private var wormholeColor = Color(255, 0, 0, 255)

    private var firstFrame: Boolean = true

    init {
        sprite = Global.getSettings().getSprite("rifts", "rifts_wormhole");
        sprite2 = Global.getSettings().getSprite("rifts", "rifts_wormhole");

        sprite.color = Color(0, 0, 0, 0)
        sprite2.color = Color(0, 0, 0, 0)

        ogHeight = sprite.height
        ogWidth = sprite.width
    }

    override fun render(layer: CampaignEngineLayers?, viewport: ViewportAPI?) {

        if (entity.memoryWithoutUpdate.get("\$WormholeColor") != null)
        {
            wormholeColor = entity.memoryWithoutUpdate.get("\$WormholeColor") as Color
        }

        if (entity.memoryWithoutUpdate.get("\$WormholeAlpha") != null)
        {
            wormholeAlpha = entity.memoryWithoutUpdate.get("\$WormholeAlpha") as Int
        }

        if (entity.memoryWithoutUpdate.get("\$WormholeSpeedMult") != null)
        {
            wormholeRotationSpeedMult = entity.memoryWithoutUpdate.get("\$WormholeSpeedMult") as Float
        }

        if (entity.memoryWithoutUpdate.get("\$WormholeSizeMult") != null)
        {
            wormholeSizeMult = entity.memoryWithoutUpdate.get("\$WormholeSizeMult") as Float
        }

        var color = Color(wormholeColor.red, wormholeColor.green, wormholeColor.blue, wormholeAlpha)

        sprite!!.renderAtCenter(entity.location.x, entity.location.y)
        sprite.color = color

        sprite2!!.renderAtCenter(entity.location.x, entity.location.y)
        sprite2.color = color

        if (Global.getSector().isPaused()) return
        sprite.angle -= 0.2f * wormholeRotationSpeedMult
        sprite2.angle -= 0.1f * wormholeRotationSpeedMult

        sprite.setSize(ogWidth * wormholeSizeMult, ogHeight * wormholeSizeMult)
        sprite2.setSize(ogWidth * wormholeSizeMult, ogHeight * wormholeSizeMult)
    }
}