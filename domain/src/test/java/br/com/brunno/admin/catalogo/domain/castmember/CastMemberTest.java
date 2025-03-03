package br.com.brunno.admin.catalogo.domain.castmember;

import br.com.brunno.admin.catalogo.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CastMemberTest {

    @Test
    void givenAValidParams_shouldCreateANewCastMember() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var actualCastMember = CastMember.create(expectedName, expectedType);

        assertNotNull(actualCastMember.getId());
        assertEquals(expectedName, actualCastMember.getName());
        assertEquals(expectedType, actualCastMember.getType());
        assertNotNull(actualCastMember.getCreatedAt());
        assertNotNull(actualCastMember.getUpdatedAt());
    }

    @Test
    void givenAnInvalidNullName_whenCreateACastMember_shouldReceiveANotification() {
        final String nullName = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var actualException = assertThrows(NotificationException.class,
                () -> CastMember.create(nullName, CastMemberType.ACTOR));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidBlankName_whenCreateACastMember_shouldReceiveANotification() {
        final var blankName = " ";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be blank";

        final var actualException = assertThrows(NotificationException.class,
                () -> CastMember.create(blankName, CastMemberType.ACTOR));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNameGreaterThen255_whenCreateACastMember_shouldReceiveANotification() {
        final var biggerName = "A".repeat(256);
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' length should be between 3 and 255";

        final var actualException = assertThrows(NotificationException.class,
                () -> CastMember.create(biggerName, CastMemberType.ACTOR));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNameLesserThan3_whenCreateACastMember_shouldReceiveANotification() {
        final var smallerName = "  ab  ";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' length should be between 3 and 255";

        final var actualException = assertThrows(NotificationException.class,
                () -> CastMember.create(smallerName, CastMemberType.ACTOR));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullType_whenCreateACastMember_shouldReceiveANotification() {
        final var name = "Vin Diesel";
        final CastMemberType nullType = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        final var actualException = assertThrows(NotificationException.class,
                () -> CastMember.create(name, nullType));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAValidParams_whenUpdateACastMember_shouldUpdateTheCastMember() {
        final var expectedName = "Vin Diesel";
        final var expectedType = CastMemberType.ACTOR;

        final var aCastMember = CastMember.create("vin d", CastMemberType.DIRECTOR);

        assertNotEquals(expectedName, aCastMember.getName());
        assertNotEquals(expectedType, aCastMember.getType());
        assertEquals(aCastMember.getCreatedAt(), aCastMember.getUpdatedAt());

        aCastMember.update(expectedName, expectedType);

        assertNotNull(aCastMember.getId());
        assertEquals(expectedName, aCastMember.getName());
        assertEquals(expectedType, aCastMember.getType());
        assertNotNull(aCastMember.getCreatedAt());
        assertNotNull(aCastMember.getUpdatedAt());
        assertTrue(aCastMember.getCreatedAt().isBefore(aCastMember.getUpdatedAt()));
    }

    @Test
    void givenAnInvalidNullName_whenUpdateACastMember_shouldReceiveANotification() {
        final String nullName = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";

        final var aCastMember = CastMember.create("vin d", CastMemberType.DIRECTOR);

        final var actualException = assertThrows(NotificationException.class,
                () -> aCastMember.update(nullName, CastMemberType.ACTOR));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidBlankName_whenUpdateACastMember_shouldReceiveANotification() {
        final var blankName = " ";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be blank";

        final var aCastMember = CastMember.create("vin d", CastMemberType.DIRECTOR);

        final var actualException = assertThrows(NotificationException.class,
                () -> aCastMember.update(blankName, CastMemberType.ACTOR));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNameGreaterThen255_whenUpdateACastMember_shouldReceiveANotification() {
        final var biggerName = "A".repeat(256);
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' length should be between 3 and 255";

        final var aCastMember = CastMember.create("vin d", CastMemberType.DIRECTOR);

        final var actualException = assertThrows(NotificationException.class,
                () -> aCastMember.update(biggerName, CastMemberType.ACTOR));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNameLesserThan3_whenUpdateACastMember_shouldReceiveANotification() {
        final var smallerName = "  ab  ";
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' length should be between 3 and 255";

        final var aCastMember = CastMember.create("vin d", CastMemberType.DIRECTOR);

        final var actualException = assertThrows(NotificationException.class,
                () -> aCastMember.update(smallerName, CastMemberType.ACTOR));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    void givenAnInvalidNullType_whenUpdateACastMember_shouldReceiveANotification() {
        final var name = "Vin Diesel";
        final CastMemberType nullType = null;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'type' should not be null";

        final var aCastMember = CastMember.create("vin d", CastMemberType.DIRECTOR);

        final var actualException = assertThrows(NotificationException.class,
                () -> aCastMember.update(name, nullType));

        assertEquals(expectedErrorCount, actualException.getErrors().size());
        assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }
}