package se.artcomputer.game;

import java.util.Collections;

import static se.artcomputer.game.GameState.*;
import static se.artcomputer.game.QuadrantContent.*;

/**
 * Super Star Trek - May, 16 1978 - Requires 24k memory
 */
public class Game {

    private static final int INITIAL_ENERGY = 3000;
    private static final String GALAXY_MAP_DIVIDER = "  ----- ----- ----- ----- ----- ----- ----- ----- ";
    private static final int INITIAL_TORPEDOES = 10;
    private static final String LRS_DIVIDER = "-------------------";
    private static final String SRS_DELIMITER = String.join("", Collections.nCopies(24, "-"));
    GameState gameState = INITIAL;
    private final GameInput scanner;

    private final GameRandom random;
    private final GameOutput output;

    private final GalaxyContent galaxyContent = new GalaxyContent();

    /**
     * Constant table for translating from a given course (1-9) to delta on S1 and S2.
     */
    private final float[][] C = new float[10][3]; // 330
    /**
     * Klingon sector position and health.
     * [1][2] : position
     * [3] : hit points remaining
     */
    private final float[][] K = new float[4][4]; // 330
    private GalaxyContent cumulativeContent;
    private final float[] damage = new float[9]; // 330
    private static final int DAMAGE_WARP_ENGINES = 1;
    private static final int DAMAGE_SHORT_RANGE_SENSORS = 2;
    private static final int DAMAGE_LONG_RANGE_SENSORS = 3;
    private static final int DAMAGE_PHASER_CONTROL = 4;
    private static final int DAMAGE_TORPEDOES = 5;
    private static final int DAMAGE_DAMAGE_CONTROL = 6;
    private static final int DAMAGE_SHIELD_CONTROL = 7;
    private static final int DAMAGE_COMPUTER = 8;
    /**
     * Current day
     */
    private float currentDate;
    /**
     * Start day
     */
    private float startDate; // 370
    /**
     * Days for mission
     */
    private int missionDays;
    /**
     * Docked at a starbase
     */
    private boolean docked = false; // 370
    /**
     * Energy available
     */
    private float energy; // 370
    //private final float E0 = energy; // 370

    /**
     * Photon torpedoes
     */
    int torpedoes;
    /**
     * Shield energy
     */
    private float shieldLevel = 0; // 440
    /**
     * Number of starbases
     */
    private int B9 = 0; // 440
    /**
     * Total number of Klingons in the galaxy?
     */
    private int K7 = 0; // 1200
    private String X$ = ""; // 440
    private String X0$ = " IS "; // 440

    private int I; // TODO: What is is this?
    private int Z4;
    private int Z5;
    private int G5;
    private float D4;

    /**
     * Name of quadrant
     */
    private String G2$;
    /**
     * Course
     */
    private float C1;
    /**
     * Warp factor
     */
    private float W1; // 2360
    private float X1, X, Y; // 3110
    private float X2, Q4, Q5; // 3140
    private float H; // 4480
    private int Z3; // 8590
    private int H8; // 7400

    public Game(GameInput scanner, GameRandom random, GameOutput output) {
        this.scanner = scanner;
        this.random = random;
        this.output = output;
    }

    /***
     * Distance to Klingon[I].
     * @return distance
     */
    private double fnd() { // 470
        return Math.sqrt(Math.pow(K[I][1] - S1, 2) + Math.pow(K[I][2] - S2, 2));
    }

    /**
     * Generate a random in 1-8
     */
    private int fnr() { // 475
        return random.nextInt(8) + 1;
    }

    // 480 Initialize Enterprise's position.
    /**
     * Quadrant position
     */
    private int Q1; // 490
    /**
     * Quadrant position
     */
    private int Q2; // 490
    /**
     * Sector position
     */
    private int S1; // 490
    /**
     * Sector position
     */
    private int S2; // 490

    /**
     * Starbases in the quadrant
     */
    private int B3;
    /**
     * A quadrant has 8x8 positions. Each position is 3 characters, therefore the string is
     * 8 X 8 x 3 = 192 characters.
     * To index into it, multiply by 3 for one dimension and then by 24 (8 * 3) for the other.
     */
    private QuadrantContent quadrantContent = new QuadrantContent();

    private String A$; // 1680
    private float Z1, Z2; // 1680
    private int B4, B5; // 1880

    private void initValues() {
        energy = INITIAL_ENERGY; // 370
        shieldLevel = 0;
        currentDate = intFloor(random.nextFloat() * 20 + 20) * 100; // 370
        startDate = currentDate;
        missionDays = 25 + intFloor(random.nextFloat() * 10); // 370
        torpedoes = INITIAL_TORPEDOES; // 440
        Q1 = fnr();
        Q2 = fnr();
        S1 = fnr();
        S2 = fnr();
        for (int I = 1; I <= 9; I++) { // 530 FOR I=1TO9
            C[I][1] = 0;
            C[I][2] = 0;
        }
        C[3][1] = -1; // 540
        C[2][1] = -1; // 540
        C[4][1] = -1; // 540
        C[4][2] = -1; // 540
        C[5][2] = -1; // 540 C(5,2)=-1
        C[6][2] = -1; // 540 C(6,2)=-1

        // 600 C(1,2)=1:C(2,2)=1:C(6,1)=1:C(7,1)=1:C(8,1)=1:C(8,2)=1:C(9,2)=1
        C[1][2] = 1;
        C[2][2] = 1;
        C[6][1] = 1;
        C[7][1] = 1;
        C[8][1] = 1;
        C[8][2] = 1;
        C[9][2] = 1;

        // 670 FOR I=1TO8:D(I)=0:NEXT I
        for (int I = 1; I <= 8; I++) {
            damage[I] = 0;
        }
        cumulativeContent = new GalaxyContent();
    }

    // 710 A1$="NAVSRSLRSPHATORSHEDAMCOMXXX"
    //private final String A1$ = "NAVSRSLRSPHATORSHEDAMCOMXXX";

    private int R1, R2;

    // 810 REM Setup what exists in galaxy...
    private void setupGalaxy() {
        // 815 REM K3 = # Klingons B3 = # Starbases S3 = # Stars
        for (int I = 1; I <= 8; I++) {
            // 820 FOR I=1TO8: FOR J=1TO8:K3=0:Z(I,J)=0:R1=RND(1)
            for (int J = 1; J <= 8; J++) {
                int klingons = 0;
                float randomLevel = random.nextFloat();
                if (randomLevel > 0.98) {
                    // 850 IFR1>.98 THEN K3=3:K9=K9+3:GOTO 980
                    klingons = 3;
                } else if (randomLevel > 0.95) {
                    // 860 IFR1>.95 THEN K3=2:K9=K9+2:GOTO 980
                    klingons = 2;
                } else if (randomLevel > 0.8) {
                    // 870 IFR1>.80 THEN K3=1:K9=K9+1
                    klingons = 1;
                }
                // 980 B3=0:IF RND(1) > .96 THEN B3=1:B9=B9+1
                int bases = 0;
                if (random.nextFloat() > 0.96) {
                    bases = 1;
                    B9 = B9 + 1;
                }
                // 1040 G(I,J)=K3*100+B3*10+FNR(1):NEXTJ:NEXTI:IFK9>T9 THEN T9=K9+1
                //G[I][J] = K3 * 100 + B3 * 10 + fnr();
                galaxyContent.initQuadrant(I, J, klingons, bases, fnr());
            }
        }
        missionDays = Math.max(missionDays, galaxyContent.getTotalKlingons() + 1);
        // 1100 IF B9 <> 0 THEN 1200
        if (B9 == 0) {
            // 1150 IF G(Q1,Q2)<200 THEN G(Q1,Q2)=G(Q1,Q2)+120:K9=K9+1
            //if (G[Q1][Q2] < 200) {
            if (galaxyContent.getKlingons(Q1, Q2) < 2) {
                // G[Q1][Q2] = G[Q1][Q2] + 120;  Note: probably mistake since bases is overwritten
                galaxyContent.setKlingons(Q1, Q2, galaxyContent.getKlingons(Q1, Q2) + 1);
            }
            // 1160 B9=1:G(Q1,Q2)=G(Q1,Q2)+10:Q1=FNR(1):Q2=FNR(1)
            B9 = 1;
            //G[Q1][Q2] = G[Q1][Q2] + 10; // Add a base
            galaxyContent.setBases(Q1, Q2, B9);
            Q1 = fnr();
            Q2 = fnr();
        }
        // 1200 K7=K9:IFB9<>1 THEN X$=" S": X0$=" ARE "
        K7 = galaxyContent.getTotalKlingons();
        if (B9 != 1) {
            X$ = "S";
            X0$ = " ARE ";
        }
    }

    public void step() {
        switch (gameState) {
            case INITIAL -> {
                initial();
                gameState = RUNNING;
            }
            case RUNNING -> command();
        }
    }

    private void initial() {
        initValues();
        setupGalaxy();
        println("                                  ,-----*-----,"); // 221
        println("                 ,--------------  `----  ----'");
        println("                  '--------- --'      / /");
        println("                       ,---' '-------/ /--,");
        println("                        '----------------'");
        println("");
        println("                 THE USS ENTERPRISE --~- NCC~1701");
        prompt(); // 1230
        newQuadrant1320();
    }

    private void prompt() {
        println("YOUR ORDERS ARE AS FOLLOWS:"); //1230
        println("  DESTROY THE " + galaxyContent.getTotalKlingons() + " KLINGON WARSHIPS WHICH HAS INVADED"); //1240
        println("  THE GALAXY BEFORE THEY CAN ATTACK FEDERATION HEADQUARTERS"); //1250
        println("  ON STARDATE " + (startDate + missionDays) + ". THIS GIVES YOU " + missionDays + " DAYS. THERE" + X0$); //1260
        println("  " + B9 + " STARBASE" + X$ + " IN THE GALAXY TO RESUPPLY YOUR SHIP."); //1270
        println("");
        input$("HIT RETURN WHEN YOU ARE READY.");
    }

    private void newQuadrant1320() {
        // 1310 REM Here any time a new quadrant entered.
        // 1320 Z4=Q1:Z5=Q2:K3=0:B3=0:S3=0:G5=0:D4=0.5*RND(1):Z(Q1,Q2)=G(Q1,Q2)
        Z4 = Q1;
        Z5 = Q2;
        int K3 = 0;
        int stars = 0;
        G5 = 0;
        D4 = 0.5f * random.nextFloat();
        cumulativeContent.setQuadrant(Q1, Q2, galaxyContent.getQuadrant(Q1, Q2));
        // 1390 IF Q1<1 OR Q1>8 OR Q2<1 OR Q2>8 THEN 1600
        if (!(Q1 < 1 || Q1 > 8 || Q2 < 1 || Q2 > 8)) {
            // 1430 GOSUB 9030:Print:IF T0<>T THEN 1490
            quadrantName9030();
            println("");
            if (startDate == currentDate) {
                // 1460 Print "Your mission begins with your starship located"
                // 1470 PRINT "IN THE GALACTIC QUADRANT," G2$ " QUADRANT ...": GOTO 1500
                println("YOUR MISSION BEGINS WITH YOUR STARSHIP LOCATED");
                println("IN THE GALACTIC QUADRANT," + G2$ + " QUADRANT ...");
            } else {
                // 1490 Print "Now entering " G2$ " quadrant ..."
                println("NOW ENTERING " + G2$ + " QUADRANT ...");
            }
            // 1500 PRINT:K3=INT(GCQl.Q2)*.01):B3=INT(G(Q1,Q2)*.1)-10*K3
            println("");
            K3 = galaxyContent.getKlingons(Q1, Q2);
            B3 = galaxyContent.getBases(Q1, Q2);
            // 1540 S3=G[Q1][Q2]-100 * K3 - 10 * B3: IF K3=0 THEN 1590
            stars = galaxyContent.getStars(Q1, Q2);
            if (K3 != 0) {
                // 1560 Print "COMBAT AREA    CONDITION RED": IF S>200 THEN 1590
                println("COMBAT AREA CONDITION RED");
                if (shieldLevel <= 200) {
                    // 1580 Print "     Shields dangerously low"
                    println("     SHIELDS DANGEROUSLY LOW");
                }
            }
            // 1590 FOR I=1TO3: K(I,1)=0:K(I,2)=0: NEXT I
            for (int I = 1; I <= 3; I++) {
                K[I][1] = 0;
                K[I][2] = 0;
            }
        }
        // 1600
        for (int I = 0; I <= 3; I++) {
            K[I][3] = 0;
        }
        //Q$ = Z$ + Z$ + Z$ + Z$ + Z$ + Z$ + Z$ + left$(Z$, 17); // 7 * 25 + 17 = 192
        quadrantContent = new QuadrantContent();
        // 1660 REM Position Enterprise in quadrant, then place "K3" Klingons, &
        // 1670 REM "B3" starbases, & "S3" stars elsewhere.
        // 1680 A$="<*>":Z1=S1:Z2=S2:GOSUB 8670:IF K3<1 THEN 1820
        insertIconInQuadrantString8670(S1, S2, STARSHIP_ICON);
        if (K3 >= 1) {
            // 1720 FOR I=1TOK3: GOSUB 8590: A$="+K+":Z1=R1:Z2=R2
            for (int I = 1; I <= K3; I++) {
                findEmptyPlaceInQuadrant8590();
                // 1780 GOSUB 8670: K(I,1)=R1:K(I,2)=R2;K(I,3)=S9*0.5+RND(1):NEXTI
                insertIconInQuadrantString8670(R1, R2, KLINGON_ICON);
                K[I][1] = R1;
                K[I][2] = R2;
                K[I][3] = 200 * 0.5F + random.nextFloat();
            }
        }
        // 1820 IF B3<1 THEN 1910
        if (B3 >= 1) {
            // 1880 GOSUB 8590: A$=">!<":Z1=R1:B4=R1:Z2=R2:B5=R2:GOSUB 8670
            findEmptyPlaceInQuadrant8590();
            B4 = R1;
            B5 = R2;
            insertIconInQuadrantString8670(R1, R2, STARBASE_ICON);
        }
        // 1910 FOR I=1TOS3:GOSUB 8590:A$=" * ":Z1=R1:Z2=R2:GOSUB 8670:NEXTI
        for (int I = 1; I <= stars; I++) {
            findEmptyPlaceInQuadrant8590();
            insertIconInQuadrantString8670(R1, R2, STAR_ICON);
        }
        // 1980 GOSUB 6430
        shortRangeSensors6430();
    }

    private void command() {
        // 1990 IF S+E > 10 THEN IF E>10 OR D[7]=0 THEN 2060
        if (shieldLevel + energy <= 10) {
            if (energy <= 10) {
                // 2020 2030 2040 2050
                println("");
                println("** FATAL ERROR ** YOU'VE JUST STRANDED YOUR SHIP IN ");
                println("SPACE");
                println("YOU HAVE INSUFFICIENT MANEUVERING ENERGY,");
                println(" AND SHIELD CONTROL");
                println("IS PRESENTLY INCAPABLE OF CROSS");
                println("-CIRCUITING ENGINE ROOM");
                // GOTO 6220
                goto6220();
                return;
            }
        }
        // 2060 INPUT "COMMAND"; A$
        A$ = input$("COMMAND ");

        // 2080 FOR I=1TO9:IF LEFT$(A$,3)<>MID$(A1$, 3*I-2,3) THEN 2160
        // 2140 ON I GOTO 2300,1980,4000,4260,4700,5530,5690,7290,6270
        // 2160 NEXT I: PRINT "ENTER ONE OF THE FOLLOWING"
        switch (A$.toUpperCase()) {
            case "NAV" -> gotoNAV2300();
            case "SRS" -> shortRangeSensors6430(); // GOTO 1980
            case "LRS" -> longRangeSensors();
            case "PHA" -> phaserControl();
            case "TOR" -> photonTorpedo();
            case "SHE" -> gotoSHE5530();
            case "DAM" -> gotoDAM5690();
            case "COM" -> gotoCOM7290();
            case "XXX" -> gotoXXX6270();
            case "GRS" -> galaxySensor();
            default -> help();
        }
    }


    private void gotoNAV2300() {
        // 2290 REM Course control begins here
        // 2300
        C1 = inputF("Course (0-9): ");
        if (C1 == 9) {
            C1 = 1;
        }
        if (C1 < 1 || C1 > 9) {
            println(" LT. SULU REPORTS 'INCORRECT COURSE DATA, SIR!'");
        } else {
            X$ = "8"; // 2350 NSeF"DCG1)<8OTH"ENX:S=L"ge2"
            if (damage[DAMAGE_WARP_ENGINES] < 0) {
                X$ = "0.2";
            }
            // 2360
            W1 = inputF("WARP FACTOR (0 - " + X$ + "): ");
            if (W1 != 0) {
                if (damage[DAMAGE_WARP_ENGINES] < 0 && W1 > 0.200001) {
                    // 2470
                    println("WARP ENGINES ARE DAMAGED, MAXIMUM SPEED = WARP 0.2");
                } else {
                    if (W1 > 0 && W1 <= 8) {
                        // 2490
                        int stepsN = intFloor(W1 * 8 + 0.5);
                        if (energy - stepsN < 0) {
                            println("ENGINEERING REPORTS 'INSUFFICIENT ENERGY AVAILABLE");
                            println("  FOR MANEUVERING AT WARP " + W1 + "!");
                            // 2530
                            if (!(shieldLevel < stepsN - energy || damage[DAMAGE_DAMAGE_CONTROL] > 0)) {
                                println("DEFLECTOR CONTROL ROOM ACKNOWLEDGES " + shieldLevel + " UNITS OF ENERGY");
                                println("  PRESENTLY DEPLOYED TO SHIELDS.");
                            }
                        }
                        klingons();
                        moveStarShip(stepsN);
                    }
                }
            }
        }
    }

    /**
     * 2580 REM KLINGONS MOVE/FIRE ON MOVING STARSHIP ...
     */
    private void klingons() {
        // 2590 FOR I=1TOK3: IF K(I,3) = 0 THEN 2700
        for (int I = 1; I <= galaxyContent.getKlingons(Q1, Q2); I++) {
            if (K[I][3] != 0) {
                // 2610
                insertIconInQuadrantString8670(K[I][1], K[I][2], EMPTY_ICON);
                findEmptyPlaceInQuadrant8590();
                K[I][1] = Z1;
                K[I][2] = Z2;
                insertIconInQuadrantString8670(K[I][1], K[I][2], KLINGON_ICON);
            }
        } // 2700 NEXT I: GOSUB 6000
        klingonsShooting6000();
        float d1 = 0;
        // 2700
        float d6 = W1;
        if (W1 >= 1) {
            d6 = 1;
        }
        // 2770 FORI=1TO8:IFD(I)>=0THEN2880
        for (int index = 1; index <= 8; index++) {
            if (damage[index] < 0) {
                // 2790 D(I)=D(I)+D6:IFD(I)>-.1 AND D(I)<0 THEN D(I)=.1 : GOTO 2880
                damage[index] = damage[index] + d6;
                if (damage[index] > -0.1 && damage[index] < 0) {
                    damage[index] = -0.1F;
                } else {
                    if (damage[index] >= 0) {
                        if (d1 != 1) {
                            d1 = 1;
                        }
                        println("DAMAGE CONTROL REPORT:  ");
                        println("\t\t\t\t\t\t\t");
                        R1 = index;
                        deviceName8790();
                        println(G2$);
                        println(" REPAIR COMPLETED.");
                    }
                }
            }
        }// 2880 NEXT I: IF RND(1) > 0.2 THEN 3070
        if (random.nextFloat() <= 0.2) {
            R1 = fnr(); // 2910
            int index = intFloor(R1);
            if (random.nextFloat() < 0.6) {
                damage[index] = damage[index] - (random.nextFloat() * 5 + 1);
                println("DAMAGE CONTROL REPORT: " + deviceName8790(index) + " DAMAGED");
                println("");
            } else {
                // 3000
                damage[index] = damage[index] + (random.nextFloat() * 3 + 1);
                println("DAMAGE CONTROL REPORT: " + deviceName8790(index) + " STATE OF REPAIR IMPROVED.");
                println("");
            }
        } // 3070
    }

    // 3060
    private void moveStarShip(int stepsN) {
        // 3070 A$ = "   " ...
        insertIconInQuadrantString8670(S1, S2, EMPTY_ICON);
        // 3110
        int C1int = intFloor(C1);
        X1 = C[C1int][1] + (C[C1int + 1][1] - C[C1int][1]) * (C1 - C1int);
        X = S1;
        Y = S2;
        // 3140 C(C1,2) + (C(C1 + 1, 2) - C(C1, 2)) * (C1 - INT(C1)):Q4=Q1:Q5=Q2
        X2 = C[C1int][2] + (C[C1int + 1][2] - C[C1int][2]) * (C1 - C1int);
        Q4 = Q1;
        Q5 = Q2;
        // 3170 FORI=1TON:Si=Si+X1:S2=S2+X2:1FSI<LORS1>=9ORS2<1ORS2>=9THEN 3500
        boolean shutdown = false;
        for (int I = 1; I <= stepsN; I++) {
            S1 = S1 + intFloor(X1);
            S2 = S2 + intFloor(X2);
            if (S1 < 1 || S1 >= 9 || S2 < 1 || S2 >= 9) {
                exceededQuadrantLimits3500(stepsN);
                return;
            } else {
                String check = quadrantContent.get(S1, S2);
                if (!check.equals(EMPTY_ICON)) {
                    S1 = intFloor(S1 - X1);
                    S2 = intFloor(S2 - X2);
                    println("WARP ENGINES SHUT DOWN AT");
                    println("SECTOR " + S1 + "," + S2 + " DUE TO BAD NAVIGATION.");
                    shutdown = true;
                    break;
                }
            }
        } // 3360
        if (!shutdown) {
            S1 = intFloor(S1);
            S2 = intFloor(S2);
        }
        goto3370(stepsN);
    }

    private void goto3370(int stepsN) {
        // 3370
        insertIconInQuadrantString8670(S1, S2, STARSHIP_ICON);
        maneuverEnergy3910(stepsN);
        // 3370
        float timeUsed = 1;
        if (W1 < 1) {
            timeUsed = 0.1F * intFloor(10 * W1);
        }
        // 3450 T=T+T8:IFT>T0+T9 THEN 6220
        currentDate = currentDate + intFloor(timeUsed);
        if (currentDate > startDate + missionDays) {
            goto6220(); // Ran out of time
        }
        // 3470 REM See if docked, then command
        // 3480 GOTO 1980
        shortRangeSensors6430();
    }

    // 3498 REM EXCEEDED QUADRANT LIMITS
    private void exceededQuadrantLimits3500(int stepsN) {
        // 3500 X=8*Q1+X+N*X1:Y=8*Q2+Y+N*X2:Q1=INT(X/8):Q2=INT(Y/8):S1=INT(X-Q1*8)
        X = 8 * Q1 + X + stepsN * X1;
        Y = 8 * Q2 + Y + stepsN * X2;
        Q1 = intFloor(X / 8);
        Q2 = intFloor(Y / 8);
        S1 = intFloor(X - Q1 * 8);
        // 3550 S2=INT(Y-Q2*8): IF S1=0 THEN Q1=Q1-1:S1=8
        S2 = intFloor(Y - Q2 * 8);
        if (S1 == 0) {
            Q1 = Q1 - 1;
            S1 = 8;
        }
        // 3590 IF S2=0 THEN Q2=Q2-1:S2=8
        if (S2 == 0) {
            Q2 = Q2 - 1;
            S2 = 8;
        }
        // 3620 X5=0:IF Q1<1 THEN X5=1:Q1=1:S1=1
        // 3620
        int x5 = 0;
        if (Q1 < 1) {
            x5 = 1;
            Q1 = 1;
            S1 = 1;
        }
        // 3670 IF Q1>8 THEN X5=1:Q1=8:S1=8
        if (Q1 > 8) {
            x5 = 1;
            Q1 = 8;
            S1 = 8;
        }
        // 3710 IF Q2<1 THEN X5=1:Q2=1:S2=1
        if (Q2 < 1) {
            x5 = 1;
            Q2 = 1;
            S2 = 1;
        }
        // 3750 IF Q2>8 THEN X5=1:Q2=8:S2=8
        if (Q2 > 8) {
            x5 = 1;
            Q2 = 8;
            S2 = 8;
        }
        // 3790 IF X5=0 THEN 3860
        if (x5 != 0) {
            // 3800 PRINT"LT-UHURA REPORTS MESSAGE FROM STARFLEET COMMAND:"
            // 3810 PRINT"??? PERMISSION TO ATTEMPT CROSSING OF GALACTIC PERIMETER"
            // 3820 PRINT" IS HEREBY *DENIED*. SHUT DOWN YOUR ENGINES.'"
            // 3830 PRINT"CHIEF ENGINEER SCOTT REPORTS ???WARP ENGINES SHUT DOWN"
            println("LT-UHURA REPORTS MESSAGE FROM STARFLEET COMMAND:");
            println("??? PERMISSION TO ATTEMPT CROSSING OF GALACTIC PERIMETER");
            println(" IS HEREBY *DENIED*. SHUT DOWN YOUR ENGINES.'");
            println("CHIEF ENGINEER SCOTT REPORTS ???WARP ENGINES SHUT DOWN'");
            // 3840 PRINT" AT SECTOR";S1;",";S2;"OF QUADRANT";Q1;",";Q2;"-'"
            println(" AT SECTOR " + S1 + "," + S2 + " OF QUADRANT " + Q1 + "," + Q2 + " '");
            // 3850 IF T>T0+T9 THEN 6220
            if (currentDate > startDate + missionDays) {
                goto6220();
            }
        }
        // 3860 IF8*Q1+Q2=8*Q4+Q5 THEN 3370
        if (8 * Q1 + Q2 == 8 * Q4 + Q5) {
            goto3370(stepsN);
        } else {
            // 3870 T=T+1:GOSUB 3910: GOTO 1320
            currentDate = currentDate + 1;
            maneuverEnergy3910(stepsN);
            // goto 1320 is entering a new quadrant.
            newQuadrant1320();
        }
    }

    private void maneuverEnergy3910(int stepsN) {
        //        3900 REM MANEUVER ENERGY S/R **
        //        3910 E=E-N-10:IFE>=0THEN RETURN
        //        3930 PRINT"SHIELD CONTROL SUPPLIES ENERGY TO COMPLETE THE MANEUVER."
        //        3940 S=S+E: E=0: IFS<=0THEN S=0
        //        3980 RETURN
        energy = energy - stepsN - 10;
        if (energy < 0) {
            println("SHIELD CONTROL SUPPLIES ENERGY TO COMPLETE THE MANEUVER.");
            shieldLevel = shieldLevel + energy;
            energy = 0;
            if (shieldLevel <= 0) {
                shieldLevel = 0;
            }
        }
    }

    private void longRangeSensors() {
        //  3990 REM LONG RANGE SENSOR SCAN CODE
        //  4000 IFD(3)<0 THEN PRINT "LONG RANGE SENSORS ARE INOPERABLE'":GOT0 1990
        if (damage[DAMAGE_LONG_RANGE_SENSORS] < 0) {
            println("LONG RANGE SENSORS ARE INOPERABLE'");
        } else {
            //  4030 PRINT"LONG RANGE SCAN FOR QUADRANT";Q1;",";Q2
            //  4040 O1$="-------------------":PRINT O1$
            println("LONG RANGE SCAN FOR QUADRANT " + Q1 + "," + Q2);
            println(LRS_DIVIDER);
            //  4060 FOR I=Q1-1 TO Q1+1:N(1)=-1:N(2)=-2:N(3)=-3:FOR J=Q2-1T0Q2+1
            float[] N = new float[4]; // Why an array with same name as a single?
            for (int I = Q1 - 1; I <= Q1 + 1; I++) {
                N[1] = -1;
                N[2] = -2;
                N[3] = -3;
                for (int J = Q2 - 1; J <= Q2 + 1; J++) {
                    //  4120 IF I>0 AND I<9 AND J>D AND J<9 THEN N(J-Q2+2)=G(I,J):Z(I,J)=G(I,J)
                    if (I > 0 && I < 9 && J > 0 && J < 9) {
                        N[J - Q2 + 2] = galaxyContent.numeric(I, J);
                        cumulativeContent.setQuadrant(I, J, galaxyContent.getQuadrant(I, J));
                    }
                }
                // 4180 NEXTJ: FOR L=1TO3:PRINT": ???;:IFN(L)<0 THEN PRINT"*** ";: GOTO4230
                for (int L = 1; L <= 3; L++) {
                    print(":");
                    if (N[L] < 0) {
                        print(" *** ");
                    } else {
                        // 4210 PRINT RIGHT$(STR$(N(L)+1000),3);" ???;
                        print(String.format(" %03.0f ", N[L]));
                    }
                    // 4230 NEXTL: PRINT":": PRINT O1$:NEXT1: GOTO 1990

                }
                println(":");
                println(LRS_DIVIDER);
            }
        }
    }

    private void galaxySensor() {
        // For debugging
        for (int i = 1; i <= 8; i++) {
            print(String.format("%4d", i));
        }
        println("");
        for (int i = 1; i <= 8; i++) {
            println("----------------------------------");
            print("" + i);
            for (int j = 1; j <= 8; j++) {
                print(String.format(" %03.0f", galaxyContent.numeric(i, j)));
            }
            println("");
        }
        println(galaxyContent.statistics());
    }

    /**
     * 4260 REM PHASER CONTROL CODE BEGINS HERE
     */
    private void phaserControl() {
        // 4260 IFD(4)<0THENPRINT'PHASERS INOPERATI VE":GOTO1996
        if (damage[DAMAGE_PHASER_CONTROL] < 0) {
            println("PHASERS INOPERATIVE");
            return;
        }
        // 4265 TFK3>0 THEN 4330
        if (galaxyContent.getKlingons(Q1, Q2) <= 0) {
            noEnemy4270();
            return;
        }

        // 4330 IFDCS)<@THEN PRINT "COMPUTER FAILURE HANPERS ACCURACY"
        if (damage[DAMAGE_COMPUTER] < 0) {
            println("COMPUTER FAILURE HAMPERS ACCURACY");
        }
        // 4350 PRINT"PHASERS LOCKED ON TARGETS "3
        println("PHASERS LOCKED ON TARGETS ");
        do {
            // 4360 PRINT" ENERGY AVAILABLE =";E; "UNITS"
            println(" ENERGY AVAILABLE =" + energy + "UNITS");
            // 4370 INPUT'NUMBER OF UNITS TO FIRE";X:IF X<=0 THEN 1990
            X = input("NUMBER OF UNITS TO FIRE: ");
            if (X <= 0) {
                return;
            }
            // 4400 IFE-X<0THEN 4360
        } while (energy - X < 0);
        // 4410 E=E-X:1FDC7)<6THEN K=XRN*DC1)
        energy = energy - X;
        if (damage[DAMAGE_SHIELD_CONTROL] < 0) {
            X = X * random.nextFloat();
        }
        // 4450 HI=INT(X/K3) :FORI= 1T03: IFK(I,3)<=0 THEN 4670
        // 4450
        int h1 = intFloor(X / galaxyContent.getKlingons(Q1, Q2));
        for (int I = 1; I <= 3; I++) {
            if (K[I][3] > 0) {
                // 4480 H=INT((H1/FND(0)))*CRN2D1FCH>.11S*)KC+Ls23)THEN 4530
                H = intFloor((h1 / fnd()) * (random.nextFloat() + 2));
                if (H <= 0.15 * K[I][3]) {
                    // 4500 PRINT"SENSORS SHOW NO DAMAGE TO ENEMY AT";K(I,1);",";K(I,2):GOTO 4670
                    println("SENSORS SHOW NO DAMAGE TO ENEMY AT " + K[I][1] + "," + K[I][2]);
                } else {
                    // 4530 K(I,3)=K(I,3)-H:PRINT H;" UNIT HIT ON KLINGON IN SECTOR";K(I,1);",";K(I,2)
                    K[I][3] = K[I][3] - H;
                    println(H + " UNIT HIT ON KLINGON IN SECTOR " + K[I][1] + "," + K[I][2]);
                    // 4550 PRINTK(C I+ 2)s1 FKCLs 3)<=@THENPRINT"*** KLINGON DESTROYED ***"':GOTO 4580
                    if (K[I][3] > 0) {
                        // 4560 PRINT" (SENSORS SHOW'SKCI303."UNITS REMAINING)":GOTO 4670
                        println("  (SENSORS SHOW " + K[I][3] + " UNITS REMAINING)");
                    } else {
                        println("*** KLINGON DESTROYED ***");
                        // 4580 K3=K3-1:K9=K9-1:Z1=K(I,1):Z2=K(I,2):A$="   ":GOSUB 8670
                        insertIconInQuadrantString8670(K[I][1], K[I][2], EMPTY_ICON);
                        // 4650 K(I,3)=0:G(Q1,Q2)=G(Q1,Q2)-100:Z(Q1,Q2)=G(Q1,Q2):IF K9<=0 THEN 6370
                        K[I][3] = 0;
                        //G[Q1][Q2] = G[Q1][Q2] - 100; // Count down on Klingons in this sector
                        galaxyContent.setKlingons(Q1, Q2, galaxyContent.getKlingons(Q1, Q2) - 1); // Count down on Klingons in this sector
                        cumulativeContent.setQuadrant(Q1, Q2, galaxyContent.getQuadrant(Q1, Q2));
                        if (galaxyContent.getTotalKlingons() <= 0) {
                            victory6370();
                        }
                    }
                }
            }
        }
        // 4670 NEXT I: GOSUB 6000: GOTO 1990
        klingonsShooting6000();
    }

    private void noEnemy4270() {
        // 4270 PRINT'SCI ENCE OFFICER SPOCK REPORTS ???SENSORS SHOW NG ENEMY SHIPS"
        // 4280 PRINT" IN THIS QUADRAN "'3G0TO1996
        println("SCIENCE OFFICER SPOCK REPORTS ???SENSORS SHOWING NO ENEMY SHIPS");
        println(" IN THIS QUADRANT'");
    }

    private void photonTorpedo() {
        // 4690 REM PHOTON TORPEDO CODE BEGINS HERE
        // 4700 IF P<=0 THEN PRINT"ALL PHOTON TORPEDOES EXPENDED":
        if (torpedoes <= 0) {
            println("ALL PHOTON TORPEDOES EXPENDED");
            return;
        }
        // 4730 IF D(5) < 0 THEN
        if (damage[DAMAGE_TORPEDOES] < 0) {
            println("PHOTON TUBES ARE NOT OPERATIONAL");
            return;
        }
        // 4760 INPUT PHOTON TORPEDO COURSE (1-9)
        C1 = inputF("PHOTON TORPEDO COURSE (1-9): ");
        if (C1 == 9) {
            C1 = 1;
        }
        if (C1 < 1 || C1 > 9) {
            println("ENSIGN CHEKOV REPORTS, 'INCORRECT COURSE DATA, SIR!");
            return;
        }
        // 4850 X1=C(C1,1)..
        int C1int = intFloor(C1);
        X1 = C[C1int][1] + (C[C1int + 1][1] - C[C1int][1]) * (C1 - C1int);
        energy = energy - 2;
        torpedoes = torpedoes - 1;
        X2 = C[C1int][2] + (C[C1int + 1][2] - C[C1int][2]) * (C1 - C1int);
        X = S1;
        Y = S2;
        // 4910
        println("TORPEDO TRACK:");
        // 4920
        boolean missed = false;
        // 4920
        int y3;
        int x3;
        do {
            X = X + X1;
            Y = Y + X2;
            x3 = intFloor(X + 0.5);
            y3 = intFloor(Y + 0.5);
            // 4960 IFX3<1ORX3>8 ... THEN 5490
            if (!(x3 < 1 || x3 > 8 || y3 < 1 || y3 > 8)) {
                // 5000 PRINT "  ...
                println("        " + x3 + "," + y3);
                // 5050 IF Z3 <> 0 THEN 4920
            } else {
                missed = true;
            }
        } while (!missed && checkForIcon8830(X, Y, EMPTY_ICON));
        if (missed) {
            // 5490 PRINT "TORPEDO MISSED":GOSUB6000
            println("TORPEDO MISSED");
            klingonsShooting6000();
            return;
        }
        switch (quadrantContent.get(intFloor(X), intFloor(Y))) {
            case KLINGON_ICON -> {
                println("*** KLINGON DESTROYED ***");
                galaxyContent.setKlingons(Q1, Q2, galaxyContent.getKlingons(Q1, Q2) - 1);
                if (galaxyContent.getTotalKlingons() <= 0) {
                    victory6370();
                }
                boolean breakOut = false;
                for (I = 1; I <= 3; I++) {
                    if (x3 == K[I][1] && y3 == K[I][2]) {
                        breakOut = true;
                        break;
                    }
                }
                if (!breakOut) {
                    I = 3;
                }
                // 5190 K(I,3)=0:GOTO 5430
                K[I][3] = 0;
            }
            case STAR_ICON -> {
                println("STAR AT " + x3 + "," + y3 + " ABSORBED TORPEDO ENERGY.");
                klingonsShooting6000();
                return;
            }
            case STARBASE_ICON -> {
                println("*** STARBASE DESTROYED ***");
                B3 = B3 - 1;
                B9 = B9 - 1;
                // 5360 IFB9>0.. THEN 5400
                if (!(B9 > 0) || galaxyContent.getTotalKlingons() > currentDate - startDate - missionDays) {
                    println("THAT DOES IT, CAPTAIN!! YOU ARE HEREBY RELIEVED OF COMMAND");
                    println("AND SENTENCED TO 99 STARDATES AT HARD LABOR ON CYGNUS 12!!");
                    gotoXXX6270();
                } else {
                    // 5400
                    println("STARFLEET COMMAND REVIEWING YOUR RECORD TO CONSIDER");
                    println("COURT MARTIAL!");
                    docked = false;
                }
            }
        }
        // 5430
        insertIconInQuadrantString8670(X, Y, EMPTY_ICON);
        // 5470 G(Q1,Q2)=K3*100..
        galaxyContent.setBases(Q1, Q2, B3);
        cumulativeContent.setQuadrant(Q1, Q2, galaxyContent.getQuadrant(Q1, Q2));
        klingonsShooting6000();
    }

    private void gotoSHE5530() {
        // REM 5520 SHIELD CONTROL
        // 5530 IFD( 79<0THENPRINT??? SHIELD CONTROL INOPERABLE: GOTO199G
        if (damage[DAMAGE_SHIELD_CONTROL] < 0) {
            println("SHIELD CONTROL INOPERABLE");
            return;
        }
        // 5562 PRINT ENERGY AVAILABLE ="J E+S;:1NPUT"NUMBER OF UNITS TO SHI ELDS"3x%
        println("ENERGY AVAILABLE = " + (energy + shieldLevel));
        float newShieldLevel = inputF("NUMBER OF UNITS TO SHIELDS: ");
        if (newShieldLevel < 0 || shieldLevel == newShieldLevel) {
            println("<SHIELDS UNCHANGED>");
            return;
        }
        if (newShieldLevel > energy + shieldLevel) {
            println("SHIELD CONTROL REPORTS 'THIS IS NOT THE FEDERATION TREASURY'");
            println("<SHIELDS UNCHANGED>");
            return;
        }
        energy = energy + shieldLevel - newShieldLevel;
        shieldLevel = newShieldLevel;
        println("DEFLECTOR ROOM CONTROL REPORT:");
        println("   'SHIELDS NOW AT " + intFloor(shieldLevel) + " UNITS PER YOUR COMMAND.'");
    }

    private void gotoDAM5690() {
        if (damage[DAMAGE_DAMAGE_CONTROL] < 0) {
            println("DAMAGE CONTROL NOT AVAILABLE");
            if (!docked) {
                return;
            }
        }
        if (docked) {
            // 5720
            float repairCost = 0;
            for (int index = 1; index <= 8; index++) {
                if (damage[index] < 0) {
                    repairCost = repairCost + 0.1f;
                }
            }
            if (repairCost > 0) {
                // 5780
                println("");
                repairCost = repairCost + D4;
                if (repairCost >= 1) {
                    repairCost = 0.9f;
                }
                println("TECHNICIANS STANDING BY TO EFFECT REPAIRS TO YOUR SHIP");
                println("ESTIMATED TIME TO REPAIR: " + 0.01 * intFloor(100 * repairCost) + " STARDATES");
                String answer = input$("WILL YOU AUTHORIZE THE REPAIR ORDER (Y/N)");
                if (answer.equalsIgnoreCase("y")) {
                    for (int I = 1; I <= 8; I++) {
                        if (damage[I] < 0) {
                            damage[I] = 0;
                        }
                    }
                    currentDate = currentDate + repairCost + 0.1f;
                }
            }
        }

        // 5910
        println("");
        println(String.format("%20s %s", "DEVICE", "STATE OF REPAIR"));
        for (int I = 1; I <= 8; I++) {
            println(String.format("%20s %3f", deviceName8790(I), intFloor(damage[I] * 100) * 0.01));
        }
        println("");
    }


    private void klingonsShooting6000() {
        //5990 REM KLINGONS SHOOTING
        //6000 IFK3<=0 THEN RETURN
        if (galaxyContent.getKlingons(Q1, Q2) <= 0) {
            return;
        }
        // 6010 TFDG<>QTHENPRINT'STARBASE SHIELDS PROTECT THE ENTERPRISE": RETURN
        if (docked) {
            print("STARBASE SHIELDS PROTECT THE ENTERPRISE");
            return;
        }
        // 6040 FORI= 1TO3: IFK(I, 3) <= 0 THEN 6200
        for (int I = 1; I <= 3; I++) {
            if (K[I][3] > 0) {
                // 6060 H=INTCCKCEs 3) /FNDC 1) *C24+PNDC 120): SsS-HikCls 3=KCLs 3) /C3+RND(0)
                H = intFloor((K[I][3] / fnd()) * 2 + random.nextFloat());
                shieldLevel = shieldLevel - H;
                K[I][3] = K[I][3] / (3 + random.nextFloat()); // Here RND(0) is in the code as opposed to RND(1).
                // 6080 PRINT H;"UNIT HIT ON ENTERPRISE FROM SECTOR";K(I,1);",";K(I,2)"
                println(H + "UNIT HIT ON ENTERPRISE FROM SECTOR " + intFloor(K[I][1]) + "," + intFloor(K[I][2]));
                // 6090 IFS<=0 THEN 6240
                if (shieldLevel <= 0) {
                    enterpriseDestroyed6240(); // Ouch, we're done
                    return;
                } else {
                    // 6100 PRINT" <SHIELDS DOWN TO"S Ss ???UNL TS> "3 :IFH<20 THEN 6200
                    println(" <SHIELDS DOWN TO " + shieldLevel + " UNITS> ");
                    if (H < 20) {
                        continue;
                    }
                    // 6120 IF RND(1)>.60 OR H/S<=.02 THEN 6200
                    if (random.nextFloat() > 0.6 || H / shieldLevel <= 0.02) {
                        continue;
                    }
                    // 6140 R1=FNR(1):D(R1)= FNRC1) 2DORI = DCRL) -H/S- ??S*PNDC 1) :GOSUB 8790
                    R1 = fnr();
                    int index = intFloor(R1);
                    damage[index] = damage[index] - H / shieldLevel - 0.5F * random.nextFloat();
                    // 6170 PRINT"DAMAGE CONTROL REPORTS ???";G2$;" DAMAGED BY THE HIT'"
                    println("DAMAGE CONTROL REPORTS ???" + deviceName8790(index) + " DAMAGED BY THE HIT'");
                }
            }
        }
        // 6200 NEXTI:RETURN
    }

    private void goto6220() {
        // 6228 PRINT"LT 15 STARDATE"3 T:GOTO 6270
        println("IT IS STARDATE " + currentDate);
        gotoXXX6270();
    }

    private void gotoXXX6270() {
        // 6270 6276PRINT"THEREWERKOES"'KLISNGONBATTLECRUISERSLEFTAT"
        println("THERE WERE " + galaxyContent.getTotalKlingons() + " KLINGON BATTLE CRUISERS LEFT AT");
        println("THE END OF YOUR MISSION.");
        if (B9 == 0) {
            stop();
        }
        restart6290();
    }

    private void restart6290() {
        // 6310 PRINT" THE FEDERATION IS IN NEED OF A NEV STARSHIP COMMANDER"
        println("THE FEDERATION IS IN NEED OF A NEV STARSHIP COMMANDER");
        println("FOR A SIMILAR MISSION -- IF THERE 1S A VOLUNTEER,");
        String answer = input$("LET HIM STEP FORWARD AND ENTER ???AYE'");
        if (answer.equalsIgnoreCase("AYE")) {
            gameState = INITIAL;
        } else {
            stop();
        }
    }

    private void victory6370() {
        // 6370 PRINT"CONGRATULATION CAPTAIN! THE LAST KLINGON BATTLE CRUISER"
        println("CONGRATULATION, CAPTAIN! THE LAST KLINGON BATTLE CRUISER");
        println("MENACING THE FEDERATION HAS BEEN DESTROYED.");
        // 6400 PRINT"YOUR EFFICLENCY RATINGI15"319G@*I~1(92)K127:GOTO 6290
        println("YOUR EFFICIENCY RATING IS " + Math.pow(1000 * (K7 / (currentDate - 10F)), 2));
        restart6290();
    }

    private void enterpriseDestroyed6240() {
        // 6240 PRINT: PRINT'THE ENTERPRISE AAS BEEN DESTROYED. THE FEDERATION ws"3
        // 6250 PRINT'YILL BE CONQUENED???:GOTO 6220
        println("THE ENTERPRISE HAS BEEN DESTROYED. THE FEDERATION");
        println("WILL BE CONQUERED");
        goto6220();
    }

    private void gotoCOM7290() {
        // 7286 REM LIBRARY COMPUTER CODE
        if (damage[DAMAGE_COMPUTER] < 0) {
            println("COMPUTER DISABLED");
            return;
        }
        // 7320
        int answer = input("COMPUTER ACTIVE AND AWAITING COMMAND: ");
        println("");
        H8 = 1; // Behaviour switch?
        switch (answer) {
            case 0 -> cumulativeGalacticRecord7540();
            case 1 -> statusReport7900();
            case 2 -> calculator8070();
            case 3 -> starbaseNavData8500();
            case 4 -> directionDistanceCalculator8150();
            case 5 -> galaxyRegionMap7400();
            default -> computerHelp();
        }
    }

    private void galaxyRegionMap7400() {
        H8 = 0;
        G5 = 1;
        println("              THE GALAXY");
        goto7550();
    }

    private void cumulativeGalacticRecord7540() {
        // 7530 REM CUM GALACTIC RECORD
        // 7544 PRINT'COMPUTER RECORD. OF GALAXY FOR QUADRANT"; Q
        println("COMPUTER RECORD. OF GALAXY FOR QUADRANT " + Q1 + "," + Q2);
        goto7550();
    }

    private void goto7550() {
        println("    1     2     3     4     5     6     7     8");
        println(GALAXY_MAP_DIVIDER);
        for (int I = 1; I <= 8; I++) {
            print(String.format("%d ", I));
            // IF H8 = 0 THEN 7740
            if (H8 != 0) {
                for (int J = 1; J <= 8; J++) {
                    print("| ");
                    if (cumulativeContent.numeric(I, J) == 0) {
                        print("*** "); // GOTO 7720
                    } else {
                        print(String.format("%03.0f ", cumulativeContent.numeric(I, J)));
                    }
                } // 7720
            } else {
                // 7740 24-122S=12GOSUB9G30:5*JLENG(G=25)I):NPTRINCTT1ABC5IB~)3.G25
                Z4 = I;
                Z5 = 1;
                quadrantName9030();
                print(String.format("%-24s", G2$));
                Z5 = 5;
                quadrantName9030();
                print(String.format("%-24s", G2$));
            }
            // 7850
            println("");
            println(GALAXY_MAP_DIVIDER);
        }
    }

    private void statusReport7900() {
        // 7890 REM STATUS REPORT
        println(" STATUS REPORT ");
        X$ = "";
        if (galaxyContent.getTotalKlingons() > 1) {
            X$ = "S";
        }
        println("KLINGON" + X$ + " LEFT: " + galaxyContent.getTotalKlingons());
        println("MISSION MUST BE COMPLETED IN " + (0.1 * intFloor((startDate + missionDays - currentDate) * 10)) + " STARDATES");
        X$ = "S";
        if (B9 < 2) {
            X$ = "";
        }
        if (B9 >= 1) {
            println("THE FEDERATION IS MAINTAINING " + B9 + " STARBASE" + X$ + " IN THE GALAXY");
        } else {
            println("YOUR STUPIDITY HAS LEFT YOU ON YOUR OWN IN");
            println("   THE GALAXY -- YOU HAVE NO STARBASES LEFT!");
            // GOTO 5690
            gotoDAM5690();
        }
    }

    private void calculator8070() {
        // 8060 REM TORPEDO, BASE NAVs D/D CALCULATOR
        if (galaxyContent.getKlingons(Q1, Q2) <= 0) {
            noEnemy4270();
        }
        X$ = "";
        if (galaxyContent.getKlingons(Q1, Q2) > 1) {
            X$ = "S";
        }
        println("FROM ENTERPRISE TO KLINGON BATTLE CRUISE" + X$);
        H8 = 0;
        for (int I = 1; I <= 3; I++) {
            if (K[I][3] > 0) {
                println("DISTANCE " + new Position(S1, S2).distanceTo(new Position(intFloor(K[I][1]), intFloor(K[I][2]))));
            }
            // 8480
        }
    }

    private void directionDistanceCalculator8150() {
        // 8150 PRINT"DIRECTION/DISTANCE CALCULATOR:
        println("DIRECTION ON/DISTANCE CALCULATOR:");
        println("YOU ARE AT QUADRANT " + Q1 + "," + Q2 + " SECTOR " + S1 + "," + S2);
        println("PLEASE ENTER");
        println("INITIAL COORDINATES");
        int row1 = input("ROW: ");
        int col1 = input("COLUMN: ");
        Position pos1 = new Position(row1, col1);
        println("FINAL COORDINATES )");
        int row2 = input("ROW: ");
        int col2 = input("COLUMN: ");
        Position pos2 = new Position(row2, col2);
        println("DISTANCE " + pos1.distanceTo(pos2));
        println("DIRECTION " + pos1.directionTo(pos2));
    }

    private void starbaseNavData8500() {
        // 8500 LFB3<>@THENPRINT! FROM ENTERPRISE TO STARBASE:": ??1=B4:X=B5:G0TO1286
        if (B3 != 0) {
            println("FROM ENTERPRISE TO STARBASE:");
            Position base = new Position(B4, B5);
            Position enterprise = new Position(S1, S2);
            println("DISTANCE " + enterprise.distanceTo(base));
            println("DIRECTION " + enterprise.directionTo(base));
        } else {
            // 8510
            print("MR. SPOCK REPORTS, ???SENSORS SHOW NO STARBASES IN THIS");
            println(" QUADRANT.'");
        }

    }

    private void findEmptyPlaceInQuadrant8590() {
        // 8580 REM FIND EMPTY PLACE IN QUADRANT (FOR THINGS)
        // 8590 RI= FNC 1): R2=FNRC 1) :Aas=" ":Z1=R12Z2= R2: GOSUB 8830:1 FZ3=OTHEN B590
        do {
            R1 = fnr();
            R2 = fnr();
            A$ = EMPTY_ICON;
            Z1 = R1;
            Z2 = R2;
            checkForIcon8830();
        } while (Z3 == 0);
    }

    private void computerHelp() {
        // 7360
        println("FUNCTIONS AVAILABLE FROM LIBRARY-COMPUTER:");
        println(" 0 = CUMULATIVE GALACTIC RECORD");
        println(" 1 = STATUS REPORT");
        println(" 2 = PHOTON TORPEDO DATA");
        println(" 3 = STARBASE NAV DATA");
        println(" 4 = DIRECTION/DISTANCE CALCULATOR");
        println(" 5 = GALAXY 'REGION NAME' MAP");
    }

    private void deviceName8790() {
        // 8780 REM PRINTS DEVICE NAME
        // 8790 ON R1 GOTO ...
        G2$ = deviceName8790(intFloor(R1));
    }

    private String deviceName8790(int device) {
        return switch (device) {
            case 1 -> "WARP ENGINES";
            case 2 -> "SHORT RANGE SENSORS";
            case 3 -> "LONG RANGE SENSORS";
            case 4 -> "PHASER CONTROL";
            case 5 -> "PHOTON TUBES";
            case 6 -> "DAMAGE CONTROL";
            case 7 -> "SHIELD CONTROL";
            case 8 -> "LIBRARY-COMPUTER";
            default -> "?";
        };
    }


    private void help() {
        // 2160
        println("ENTER ONE OF THE FOLLOWING:");
        println("  NAV (TO SET COURSE)");
        println("  SRS (FOR SHORT RANGE SENSOR SCAN)");
        println("  LRS (FOR LONG RANGE SENSOR SCAN)");
        println("  PHA (TO FIRE PHASERS)");
        println("  TOR (TO FIRE PHOTON TORPEDOES)");
        println("  SHE (TO RAISE OR LOWER SHIELDS)");
        println("  DAM (FOR DAMAGE CONTROL REPORT)");
        println("  COM (TO CALL ON LIBRARY-COMPUTER)");
        println("  XXX (TO RESIGN YOUR COMMAND)");
        println("");
        // 2260 GOTO 1990
    }

    private void shortRangeSensors6430() {
        Condition condition = condition();
        // 6720 ILFDC 2) >= G@THEN 6770
        if (damage[DAMAGE_SHORT_RANGE_SENSORS] < 0) {
            // 6730 PRINT: PRINT #*# SHORT RANGE SENSORS ARE OUT #4##": PRENT: RETURN
            println("");
            println("*** SHORT RANGE SENSORS ARE OUT ***");
            println("");
            return;
        }
        // 6770 Oss" ???sPRINTOL :FOR I=1T08
        println(SRS_DELIMITER);
        for (int I = 1; I <= 8; I++) {
            // 6820
            String line = quadrantContent.getRow(I);
            print(line);
            // 6830 ON I GOTO
            switch (I) {
                case 1 -> println("     STARDATE           " + intFloor(currentDate * 10) * 0.1);
                case 2 -> println("     CONDITION          " + condition.toString());
                case 3 -> println("     QUADRANT           " + Q1 + "," + Q2);
                case 4 -> println("     SECTOR             " + S1 + "," + S2);
                case 5 -> println("     PHOTON TORPEDOES   " + torpedoes);
                case 6 -> println("     TOTAL ENERGY       " + intFloor(energy + shieldLevel));
                case 7 -> println("     SHIELDS            " + intFloor(shieldLevel));
                case 8 -> println("     KLINGONS REMAINING " + galaxyContent.getTotalKlingons());
            }
        }
        println(SRS_DELIMITER);
    }

    void insertIconInQuadrantString8670(int S1, int S2, String icon) {
        quadrantContent.set(S1, S2, icon);
    }

    private void insertIconInQuadrantString8670(float S1, float S2, String icon) {
        insertIconInQuadrantString8670(intFloor(S1), intFloor(S2), icon);
    }

    /**
     * Check if an expected icon A$ is at Z1,Z2.
     * Z3 = 1 is used to indicate success.
     */
    private void checkForIcon8830() {
        // 8820 REM STRING COMPARISON IN QUADRANT ARRAY
        // 8830 Z1=INT(Z1+.5): sZO=INTCZO+.5) 2S8=(ZO=1) e340 Z1=1)*Bat 12:Z3=0
        Z1 = intFloor(Z1 + 0.5);
        Z2 = intFloor(Z2 + 0.5);
        //S8 = Math.round((Z2 - 1) * 3 + (Z1 - 1) * 24 + 1);
        // 8890 1FMI D&C QS, $8. 3)<>ASTHENRETURN
        String content = quadrantContent.get(intFloor(Z1), intFloor(Z2));
        if (content.equals(A$)) {
            Z3 = 1;
        } else {
            Z3 = 0;
        }
        // 8900 Z3=1:RETURN
    }

    private boolean checkForIcon8830(float s1, float s2, String icon) {
        return quadrantContent.get(intFloor(s1), intFloor(s2)).equals(icon);
    }

    private void stop() {
        print("STOP");
        gameState = STOPPED;
    }

    private static final String[] quadrantName1 =
            new String[]{
                    "unused",
                    "ANTARES",
                    "RIGEL",
                    "PROCYON",
                    "VEGA",
                    "CANOPUS",
                    "ALTAIR",
                    "SAGITTARIUS",
                    "POLLUX"
            };
    private static final String[] quadrantName2 =
            new String[]{
                    "unused",
                    "SIRIUS",
                    "DENEB",
                    "CAPELLA",
                    "BETELGEUSE",
                    "ALDEBARAN",
                    "REGULUS",
                    "ARCTURUS",
                    "SPICA"
            };

    private void quadrantName9030() {
        // 9010 REM Quadrant name in G2$ from Z4,Z5 = G(Q1,Q2)
        // 9030 IF Z5<=4 THEN ON Z4 GOTO 9040,9050,9060,9070,9070,9080,9100,9110
        if (Z5 <= 4) {
            G2$ = quadrantName1[Z4];
        } else {
            G2$ = quadrantName2[Z4];
        }

        // 9210 IF G5 <> 1 ON Z5 GOTO ...
        if (G5 != 1) {
            switch (Z5) {
                case 1, 5 -> G2$ += " I";
                case 2, 6 -> G2$ += " II";
                case 3, 7 -> G2$ += " III";
                case 4, 8 -> G2$ += " IV";
            }
        }
    }

    private int intFloor(float value) {
        return Math.toIntExact(Math.round(Math.floor(value)));
    }

    private int intFloor(double X) {
        return Math.toIntExact(Math.round(Math.floor(X)));
    }

    private String input$(String prompt) {
        output.print(prompt);
        return scanner.nextLine().trim();
    }

    private int input(String prompt) {
        do {
            output.print(prompt);
            String string = scanner.nextLine().trim();
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException e) {
                println(e.getMessage());
            }
        } while (true);
    }

    private float inputF(String prompt) {
        do {
            output.print(prompt);
            String string = scanner.nextLine();
            try {
                return Float.parseFloat(string);
            } catch (NumberFormatException e) {
                println(e.getMessage());
            }
        } while (true);
    }

    private void println(String s) {
        output.println(s);
    }

    private void print(String s) {
        output.print(s);
    }

    public int totalKlingons() {
        return galaxyContent.getTotalKlingons();
    }

    public Position currentSector() {
        return new Position(S1, S2);
    }

    public Position currentQuadrant() {
        return new Position(Q1, Q2);
    }

    public Condition condition() {
        Condition result;
        // 6430 FORI=S1-1TOS1+1: FOR J=S2-1TOS2t1
        docked = false;
        for (int I = S1 - 1; I <= S1 + 1; I++) {
            for (int J = S2 - 1; J <= S2 + 1; J++) {
                // 6450 TF INT C L ++. .5) >58 OR)IN T<C U+1- 5)G < LOORIRN TIC J+.W 5)T>3 C THEN 6540
                if (!(I < 1 || I > 8 || J < 1 || J > 8)) {
                    // 6490 ASH">!<"3Zfal:Z2eI:GIOG:S1FUz3+L1B THEN 6580
                    if (checkForIcon8830(I, J, STARBASE_ICON)) {
                        docked = true;
                        break;
                    }
                }
            }
        }
        // 6540 NEXT:NEXTJ:D0 = 0:GOTO6650
        if (!docked) {
            // 6650 IM(3>@THEN C$="*RED*'":GOTO 6720
            if (galaxyContent.getKlingons(Q1, Q2) > 0) {
                result = Condition.RED;
            } else {
                // 6660 CS="GREEN"':iFE<EG*??1THENCS="YELLOW"
                result = Condition.GREEN;
                if (energy < INITIAL_ENERGY * 0.1) {
                    result = Condition.YELLOW;
                }
            }
        } else {
            // 6580 D0=1:C$="DOCKED":E=E0:P=P0
            result = Condition.DOCKED;
            energy = INITIAL_ENERGY;
            torpedoes = INITIAL_TORPEDOES;
            // 6620 PRINT'SHIELDS DROPPED FOP DOCKING PURPOSES": S=0:GOTO 6720
            println("SHIELDS DROPPED FOP DOCKING PURPOSES");
            shieldLevel = 0;
        }
        return result;
    }

    public float shields() {
        return shieldLevel;
    }

    public int torpedoes() {
        return torpedoes;
    }

    public float totalEnergy() {
        return shieldLevel + energy;
    }

    public void setQuadrantContent(QuadrantContent quadrantContent) {
        this.quadrantContent = quadrantContent;
    }

    public void setCurrentKlingons(int klingons) {
        galaxyContent.setKlingons(Q1, Q2, klingons);
    }

    public void setCurrentBases(int bases) {
        galaxyContent.setBases(Q1, Q2, bases);
    }

    public void setCurrentStars(int stars) {
        galaxyContent.setStars(Q1, Q2, stars);
    }

    public int getCurrentStars() {
        return galaxyContent.getStars(Q1, Q2);
    }

    public void setCurrentQuadrant(int row, int col) {
        Q1 = row;
        Q2 = col;
        newQuadrant1320();
    }

    public void setCurrentSector(int row, int col) {
        S1 = row;
        S2 = col;
    }

    public Position getCurrentSector() {
        return new Position(S1, S2);
    }
}
