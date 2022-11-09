package lunalib.util

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.ui.LabelAPI
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.json.JSONArray
import org.json.JSONObject
import java.awt.Color

class LunaTooltip
{
    private var keywords: MutableMap<String, String> = mutableMapOf()
    private var colorKeywords: MutableMap<String, Color> = mutableMapOf()
    private var stringCSV: JSONArray = Global.getSettings().getMergedSpreadsheetDataForMod("TooltipHelperID", "data/strings/luna_strings.csv", "luna_lib")

    init {
        defaultColorKeywords()
    }

    fun addTextCSV(tooltip: TooltipMakerAPI, stringID: String): LabelAPI
    {
        var id: String = ""
        var text: String = ""
        var label: LabelAPI
        val textColor: Color = Misc.getTextColor();

        for (index in 0 until stringCSV.length())
        {
            val rows: JSONObject = stringCSV.getJSONObject(index)
            id = rows.getString("TooltipHelperID")

            if (id == stringID)
            {
                text = rows.getString("string")
                return modifyText(text, tooltip)
            }

        }
        return tooltip.addPara("Missing string: [$stringID]", textColor, 3f).also { label = it }
    }

    fun addTextJson(tooltip: TooltipMakerAPI, parentID: String, childID: String): LabelAPI
    {
        var label: LabelAPI
        val textColor = Misc.getTextColor()
        var text = ""
        text = Global.getSettings().getString(parentID, childID)

        if (text === "")
        {
            return tooltip.addPara("Missing String: [P: $parentID C: $childID]", textColor, 3f).also { label = it }
        }
        return modifyText(text, tooltip)
    }

    private fun modifyText(textToModify: String, tooltip: TooltipMakerAPI): LabelAPI
    {
        val label: LabelAPI
        val textColor: Color = Misc.getTextColor()
        val keywordArray: Array<String> = keywords.keys.toTypedArray()
        val colorKeywordArray: Array<String> = colorKeywords.keys.toTypedArray()

        var text = textToModify

        //Replaces keywords with their value
        for (i in keywordArray.indices)
        {
            text = text.replace("%" + keywordArray[i], keywords[keywordArray[i]]!!)
        }

        var listToHighlight: MutableList<String> = ArrayList()
        var colorsList: MutableList<Color> = ArrayList()
        val count = text.length - text.replace("[", "").length

        //Adds the Highlight Color
        for (i in 0 until count)
        {
            //Gets the text within []
            var substring = text.substring(text.indexOf("[") + 1, text.indexOf("]"))
            var colorKeyword = substring.substring(0, substring.indexOf(":") + 1)

            //Goes through all keywords to see which one equals the keyword within []
            for (j in colorKeywordArray.indices)
            {
                var keywordText = colorKeywordArray[j] + ":"
                if (colorKeyword == keywordText)
                {
                    colorsList.add(colorKeywords[colorKeywordArray[j]]!!)
                    text = text.replaceFirst(keywordText, "")
                    text = text.replaceFirst("\\[".toRegex(), "")
                    text = text.replaceFirst("]".toRegex(), "")
                    substring = substring.replaceFirst(keywordText, "")
                    listToHighlight.add(substring)
                    break
                }
            }
        }

        label = tooltip.addPara(text, textColor, 3f)
        label.setHighlight(*listToHighlight.toTypedArray())
        label.setHighlightColors(*colorsList.toTypedArray())

        return label
    }

    fun refreshKeyword(keyword: String, variable: String)
    {
        this.keywords.put(keyword, variable)
    }

    fun refreshKeywords(keywordsToAdd: MutableMap<String, String>)
    {
        this.keywords.putAll(keywordsToAdd)
    }

    fun addColor(keyword: String, color: Color)
    {
        this.colorKeywords.put(keyword, color)
    }

    fun addColors(colorsToAdd: MutableMap<String, Color>)
    {
        this.colorKeywords.putAll(colorsToAdd)
    }

    private fun defaultColorKeywords()
    {
        colorKeywords.put("H", Misc.getHighlightColor())
        colorKeywords.put("High", Misc.getHighlightColor())
        colorKeywords.put("P", Misc.getPositiveHighlightColor())
        colorKeywords.put("Positive", Misc.getPositiveHighlightColor())
        colorKeywords.put("N", Misc.getNegativeHighlightColor())
        colorKeywords.put("Negative", Misc.getNegativeHighlightColor())
        colorKeywords.put("B", Global.getSector().getPlayerFaction().getBrightUIColor())
        colorKeywords.put("Bright", Global.getSector().getPlayerFaction().getBrightUIColor())
        colorKeywords.put("D", Global.getSector().getPlayerFaction().getDarkUIColor())
        colorKeywords.put("Dark", Global.getSector().getPlayerFaction().getDarkUIColor())
        colorKeywords.put("T", Misc.getTextColor())
        colorKeywords.put("Text", Misc.getTextColor())
        colorKeywords.put("G", Misc.getGrayColor())
        colorKeywords.put("Gray", Misc.getGrayColor())
    }
}

