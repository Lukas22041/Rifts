package rifts.data.effects

import com.fs.starfarer.api.combat.*
import org.lazywizard.lazylib.MathUtils
import org.lazywizard.lazylib.combat.CombatUtils
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*


class LineBetweenShips(ship: ShipAPI, target: ShipAPI) : BaseCombatLayeredRenderingPlugin()
{

    var ship: ShipAPI
    var target: ShipAPI

    var renderDone = false

    var color = Color(122,139,221,255)

    init {
        this.ship = ship
        this.target = target
    }

    override fun render(layer: CombatEngineLayers?, viewport: ViewportAPI?)
    {
        val alphaMult = viewport!!.alphaMult * 1f

         GL11.glPushMatrix()

         GL11.glTranslatef(0f, 0f, 0f)
         GL11.glRotatef(0f, 0f, 0f, 1f)

         GL11.glDisable(GL11.GL_TEXTURE_2D)
         GL11.glEnable(GL11.GL_BLEND)
         GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
         GL11.glColor4ub(color.getRed().toByte(), color.getGreen().toByte(), color.getBlue().toByte(), (color.getAlpha() * alphaMult).toInt().toByte())

         GL11.glEnable(GL11.GL_LINE_SMOOTH)
         GL11.glBegin(GL11.GL_LINE_STRIP)

         GL11.glVertex2f(ship.location.x, ship.location.y) // from
         GL11.glVertex2f(target.location.x, target.location.y) // to

         GL11.glEnd()

         GL11.glPopAttrib()
         GL11.glPopMatrix()
    }

    override fun getRenderRadius(): Float {
        return 10000f
    }

    override fun getActiveLayers(): EnumSet<CombatEngineLayers> {
        return EnumSet.of(CombatEngineLayers.BELOW_PHASED_SHIPS_LAYER)
    }

    override fun isExpired(): Boolean {
        return renderDone
    }
}