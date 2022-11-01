package rifts.data.util

import com.fs.starfarer.api.Global
import java.lang.StringBuilder

class WordRedacter
{
    val languageBasicWordsKey = "\$rifts_languageBasicWords"

    fun replace(sentence: String): String
    {
        var unknownWords = Global.getSector().memoryWithoutUpdate.get("\$rifts_unknownWords") as List<String>
        var basicLanguageLearned: Boolean = Global.getSector().memoryWithoutUpdate.getBoolean(languageBasicWordsKey) ?: false
        var modifiedSentence = ""

        //Completely Undeciphered Language
        if (!basicLanguageLearned)
        {
            modifiedSentence = sentence.replace("[^ ]".toRegex(), "#")
        }
        //Knows basic words, but specialised words are missing and removed
        else
        {
            modifiedSentence = sentence
            for (word in unknownWords)
            {
                modifiedSentence = modifiedSentence.replace("(?i)$word".toRegex(), "#".repeat(word.length))
            }
            for (index in modifiedSentence.indices)
            {
                if (modifiedSentence.get(index) == 's')
                {
                    if (modifiedSentence.get(index - 1) == '#')
                    {
                        var builder = StringBuilder(modifiedSentence)
                        builder.setCharAt(index, '#')
                        modifiedSentence = builder.toString()
                    }
                }
            }
        }
        return modifiedSentence
    }
}