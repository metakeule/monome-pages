import org.monome.pages.configuration.GroovyAPI;

class MIDIFighterPage extends GroovyAPI {

    int playerY;
    int[][] world;
    int ticks;
    Random rand;
    
    void init() {
        println "MIDIFighterPage starting up";
        world = new int[sizeX()][sizeY()];
        playerY = 0;
        ticks = 0;
        rand = new Random();
    }


    void press(int x, int y, int val) {
        if (val == 1) {
            if (y < playerY && playerY > 0) {
                playerY--;
            } else if (y > playerY && playerY < sizeY() - 1) {
                playerY++;
            }
            redraw();
        }
    }

    void redraw() {
        for (int x = 0; x < sizeX(); x++) {
            for (int y = 0; y < sizeY(); y++) {
                led(x, y, world[x][y]);
            }
        }
        led(0, playerY, 1);
    }

    void clock() {
        if (ticks % 24) {
            tick();
        }
        ticks++;
    }

    void tick() {        
        for (int x = 0; x < sizeX(); x++) {
            for (int y = 0; y < sizeY(); y++) {
                if (x > 0) {
                    world[x - 1][y] = world[x][y];
                }
                if (x == 7) {
                    for (int i = 0; i < sizeY(); i++) {
                        if (rand.nextInt(sizeY() / 2) == 0) {
                            world[x][i] = 1;
                        } else {
                            world[x][i] = 0;
                        }
                    }
                }
            }
        }
        redraw();
    }

    void clockReset() {
        init();
    }
}