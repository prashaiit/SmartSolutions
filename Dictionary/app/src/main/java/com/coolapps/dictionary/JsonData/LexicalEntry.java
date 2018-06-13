package com.coolapps.dictionary.JsonData;

import java.util.List;

/**
 * Created by praskum on 9/2/2017.
 */

public class LexicalEntry
{
    public List<DerivativeOf> derivativeOf;
    public List<JsonEntry> entries;
    public List<GrammaticalFeature> grammaticalFeatures;
    public String language;
    public String lexicalCategory;
    public List<JsonNote> notes;
    public List<Pronunciation> pronunciations;
    public String text;
    public List<VariantForm3> variantForms;
}
