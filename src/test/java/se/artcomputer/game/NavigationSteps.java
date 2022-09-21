package se.artcomputer.game;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class NavigationSteps {
    @Given("a quadrant at {int},{int}")
    public void aQuadrantAt(int row, int col) {

    }

    @And("starship is located at sector {int},{int}")
    public void starshipIsLocatedAtSector(int row, int col) {
    }

    @When("issuing command NAV {float} {float}")
    public void issuingCommandNAV(float course, float warp) {
    }

    @Then("starship is moved to sector {int},{int}")
    public void starshipIsMovedSector(int row, int col) {
    }
}
