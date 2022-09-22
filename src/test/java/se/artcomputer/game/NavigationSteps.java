package se.artcomputer.game;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NavigationSteps {

    private GameInputImpl scanner;

    private static final int[] intSeries1 = new int[]
            {5, 1, 7, 2, 0, 6, 1, 0, 7, 2, 6, 1, 6, 1, 5, 5, 4};
    private static final float[] floatSeries1 = new float[]
            {0.5f, 0.9f, 0.8f, 0.7f, 0.01f, 0.3f, 0.7f, 0.2f};
    private Game game;
    private GameBufferOutput messageReceiver;

    @Given("a quadrant at {int},{int}")
    public void aQuadrantAt(int row, int col) {
        scanner = new GameInputImpl();
        GameRandom random = new GameStaticImpl(intSeries1, floatSeries1);
        messageReceiver = new GameBufferOutput();
        game = new Game(scanner, random, messageReceiver);
        command("");
        game.setCurrentQuadrant(row, col);
        QuadrantContent quadrantContent = new QuadrantContent();
        game.setQuadrantContent(quadrantContent);
        game.setCurrentKlingons(0);
        game.setCurrentBases(0);
        game.setCurrentStars(0);
    }

    @And("starship is located at sector {int},{int}")
    public void starshipIsLocatedAtSector(int row, int col) {
        game.setCurrentSector(row, col);
        game.insertIconInQuadrantString8670(row, col, QuadrantContent.STARSHIP_ICON);
    }

    @And("a star is located at sector {int},{int}")
    public void aStarIsLocatedAtSector(int row, int col) {
        game.insertIconInQuadrantString8670(row, col, QuadrantContent.STAR_ICON);
        game.setCurrentStars(1 + game.getCurrentStars());
    }

    @When("issuing command NAV {float} {float}")
    public void issuingCommandNAV(float course, float warp) {
        command("NAV", fromFloat(course), fromFloat(warp));
    }

    @Then("starship is moved to sector {int},{int}")
    public void starshipIsMovedSector(int row, int col) {
        Position currentSector = game.getCurrentSector();
        assertEquals(new Position(row, col), currentSector, "Wrong sector");
    }

    @And("starship is moved to quadrant {int},{int}")
    public void starshipIsMovedToQuadrantQuadrantRowQuadrantCol(int row, int col) {
        Position currentQuadrant = game.currentQuadrant();
        assertEquals(new Position(row, col), currentQuadrant, "Wrong quadrant");
    }

    @And("there is a message containing {string}")
    public void thereIsAMessageContaining(String message) {
        String messages = messageReceiver.getMessages();
        assertTrue(messages.contains(message), "Message not found in " + messages);
    }

    private String fromFloat(float value) {
        return Float.toString(value);
    }

    private void command(String... command) {
        scanner.setLines(command);
        game.step();
    }
}
