package org.jabref.logic.integrity;

import java.util.Collections;
import java.util.List;

import org.jabref.logic.l10n.Localization;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.database.BibDatabaseMode;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.BibtexString;
import org.jabref.model.entry.field.InternalField;
import org.jabref.model.entry.field.StandardField;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UndefinedCheckerTest {
    private UndefinedChecker checker;

    @BeforeEach
    public void setUp() {
        BibDatabaseContext databaseContext = new BibDatabaseContext();
        databaseContext.setMode(BibDatabaseMode.BIBTEX);

        BibtexString str = new BibtexString("defined", "test");
        databaseContext.getDatabase().addString(str);

        checker = new UndefinedChecker(databaseContext.getDatabase());
    }

    @Test
    void undefinedStringAtStart() {
        BibEntry entry = new BibEntry().withField(InternalField.KEY_FIELD, "#undefined# string at start")
                                       .withField(StandardField.AUTHOR, "Getulio")
                                       .withField(StandardField.YEAR, "2022");

        assertEquals(List.of(new IntegrityMessage(Localization.lang("String %0 is undefined", "#undefined#"), entry, InternalField.KEY_FIELD)), checker.check(entry));
    }

    @Test
    void undefinedStringAtMiddle() {
        BibEntry entry = new BibEntry().withField(InternalField.KEY_FIELD, "string #undefined# at middle")
                                       .withField(StandardField.AUTHOR, "Igor")
                                       .withField(StandardField.YEAR, "2022");

        assertEquals(List.of(new IntegrityMessage(Localization.lang("String %0 is undefined", "#undefined#"), entry, InternalField.KEY_FIELD)), checker.check(entry));
    }

    @Test
    void undefinedStringAtEnd() {
        BibEntry entry = new BibEntry().withField(InternalField.KEY_FIELD, "string undefined at #end#")
                                       .withField(StandardField.AUTHOR, "Gustavo")
                                       .withField(StandardField.YEAR, "2022");

        assertEquals(List.of(new IntegrityMessage(Localization.lang("String %0 is undefined", "#end#"), entry, InternalField.KEY_FIELD)), checker.check(entry));
    }

    @Test
    void stringIsDefinedStart() {
        BibEntry entry = new BibEntry().withField(StandardField.AUTHOR, "#defined# this string at start")
                                       .withField(StandardField.TITLE, "The Title")
                                       .withField(StandardField.YEAR, "2021");
        assertEquals(Collections.emptyList(), checker.check(entry));
    }

    @Test
    void stringIsDefinedMiddle() {
        BibEntry entry = new BibEntry().withField(StandardField.AUTHOR, "this string is #defined# at the middle")
                                       .withField(StandardField.TITLE, "The Title")
                                       .withField(StandardField.YEAR, "2021");
        assertEquals(Collections.emptyList(), checker.check(entry));
    }

    @Test
    void stringIsDefinedEnd() {
        BibEntry entry = new BibEntry().withField(StandardField.AUTHOR, "This string is #defined#")
                                       .withField(StandardField.TITLE, "The Title")
                                       .withField(StandardField.YEAR, "2021");
        assertEquals(Collections.emptyList(), checker.check(entry));
    }

    @Test
    void noString() {
        BibEntry entry = new BibEntry().withField(StandardField.AUTHOR, "There is no string")
                                       .withField(StandardField.TITLE, "The Title")
                                       .withField(StandardField.YEAR, "2021");
        assertEquals(Collections.emptyList(), checker.check(entry));
    }
}
