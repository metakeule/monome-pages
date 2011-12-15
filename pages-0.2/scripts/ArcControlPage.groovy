import org.monome.pages.api.GroovyAPI
import java.util.ArrayList
import org.monome.pages.api.Command
import org.monome.pages.pages.Page
import org.monome.pages.pages.GroovyPage
import org.monome.pages.configuration.MonomeConfiguration
import org.monome.pages.configuration.MonomeConfigurationFactory
import org.monome.pages.configuration.PatternBank

class ArcControlPage extends GroovyAPI {

    String prefix = "/m40h0146"

    int fingersVelo = 127
    int pcSens = 40
    int pageTurn = 0
    int pattSens = 40
    int pattLength = 0

    int stepVelo = 127
    int swing = 0
    int swingTurn = 0
    int swingSens = 40
    int length = 96
    int lengthTurn = 0
    int lengthSens = 40

    int cmd1Down = 0
    int cmd2Down = 0

    void init() {
        log("ArcControlPage starting up")
    }

    void stop() {
        log("ArcControlPage shutting down")
    }

    void delta(int enc, int delta) {
        if (enc == 0) {
            handleCommandEnc0(delta)
        }
        if (enc == 1) {
            handleCommandEnc1(delta)
        }
        if (enc == 2) {
            handleCommandEnc2(delta)
        }
        if (enc == 3) {
            handlePageChangeDelta(delta)
        }
    }

    void handleCommandEnc0(int delta) {
        MonomeConfiguration monome = getMyMonome()
        if (monome.curPage == 0) {
            pattLength += delta
            PatternBank patterns = monome.patternBanks.get(0)
            int length = patterns.patternLengths[patterns.curPattern]
            if (pattLength < -pattSens) {
                if (length > 96) {
                    length -= 96
                }
                patterns.patternLengths[patterns.curPattern] = length
                pattLength = pattSens
                redrawDevice()
            } else if (pattLength > pattSens) {
                if (length < 96 * 4) {
                    length += 96
                }
                patterns.patternLengths[patterns.curPattern] = length
                pattLength = -pattSens
                redrawDevice()
            }
        } else if (monome.curPage == 1) {
            swingTurn += delta
            if (swingTurn < -swingSens) {
                if (swing > 0) {
                    swing--
                    sendCommand(new Command("swing", swing))
                    swingTurn = swingSens
                    redrawDevice()
                }
            } else if (swingTurn > swingSens) {
                if (swing < 8) {
                    swing++
                    sendCommand(new Command("swing", swing))
                    swingTurn = -swingSens
                    redrawDevice()
                }
            }
        }
    }

    void handleCommandEnc1(int delta) {
        MonomeConfiguration monome = getMyMonome()
        if (monome.curPage == 0) {
            PatternBank patterns = monome.patternBanks.get(0)
            if (cmd1Down == 0) {
                movePlayhead(patterns, delta, patterns.curPattern)
            } else {
                for (int patternNum = 0; patternNum < patterns.numPatterns; patternNum++) {
                    movePlayhead(patterns, delta, patternNum)
                }
            }
        } else if (monome.curPage == 1) {
            lengthTurn += delta
            if (lengthTurn < -lengthSens) {
                if (length > 96) {
                    length -= 96
                    lengthTurn = lengthSens
                    sendCommand(new Command("length", length))
                    redrawDevice()
                }
            } else if (lengthTurn > lengthSens) {
                if (length < 96 * 4) {
                    length += 96
                    lengthTurn = -lengthSens
                    sendCommand(new Command("length", length))
                    redrawDevice()
                }
            }
        }
    }

    void handleCommandEnc2(int delta) {
        MonomeConfiguration monome = getMyMonome()
        if (monome.curPage == 0) {
            int newFingersVelo = fingersVelo  + delta
            if (newFingersVelo > 127) newFingersVelo = 127
            if (newFingersVelo < 0) newFingersVelo = 0
            if (newFingersVelo != fingersVelo) {
                fingersVelo = newFingersVelo
                Command cmd = new Command("velocity", fingersVelo)
                sendCommand(cmd)
                drawCommandEnc2()
            }
        } else if (monome.curPage == 1) {
            int newStepVelo = stepVelo  + delta
            if (newStepVelo > 127) newStepVelo = 127
            if (newStepVelo < 0) newStepVelo = 0
            if (newStepVelo != stepVelo) {
                stepVelo = newStepVelo
                Command cmd = new Command("velocity", stepVelo)
                sendCommand(cmd)
                drawCommandEnc2()
            }
        }
    }

    void movePlayhead(PatternBank patterns, int delta, int patternNum) {
        int pos = patterns.patternPosition[patternNum]
        int length = patterns.patternLengths[patternNum]
        int newPos = pos + delta
        if (newPos < 0) {
            newPos = length + delta
        } else if (newPos >= length) {
            newPos -= length
        }
        ArrayList<Integer> args = new ArrayList<Integer>()
        args.add(delta)
        args.add(patternNum)
        sendCommand(new Command("offsetPattern", args))
        patterns.patternPosition[patternNum] = newPos
        patterns.recordPosition[patternNum] = newPos
    }

    void resetPlayhead() {
        MonomeConfiguration monome = getMyMonome()
        PatternBank patterns = monome.patternBanks.get(0)
        for (int patternNum = 0; patternNum < patterns.numPatterns; patternNum++) {
            patterns.resetPlayhead(patternNum)
        }
    }

    void handlePageChangeDelta(int delta) {
        pageTurn += delta
        if (pageTurn < -pcSens) {
            MonomeConfiguration monome = getMyMonome()
            int nextPage = monome.curPage - 1
            if (nextPage < 0) {
                nextPage = monome.numPages - 1
            }
            monome.switchPage(nextPage)
            pageTurn = pcSens
            redrawDevice()
        }
        if (pageTurn > pcSens) {
            MonomeConfiguration monome = getMyMonome()
            int nextPage = monome.curPage + 1
            if (nextPage == monome.numPages) {
                nextPage = 0
            }
            monome.switchPage(nextPage)
            pageTurn = -pcSens
            redrawDevice()
        }
    }

    void sendCommand(Command command) {
        MonomeConfiguration monome = getMyMonome()
        Page page = monome.pages.get(monome.curPage)
        if (page instanceof GroovyPage) {
            ((GroovyPage) page).theApp.sendCommand(command)
        }
    }

    void key(int enc, int key) {
        if (enc == 2) {
            cmd2Down = key
        }
        if (enc == 1) {
            MonomeConfiguration monome = getMyMonome()
            if (monome.curPage == 0) {
                if (key == 0) {
                    resetPlayhead()
                    sendCommand(new Command("resetPlayhead", null))
                }
            }
            cmd1Down = key
        }
        redrawDevice()
    }

    void redrawDevice() {
        drawCommandEnc0()
        drawCommandEnc1()
        drawCommandEnc2()
        drawPageEnc();
    }
    
    void drawCommandEnc0() {
        MonomeConfiguration monome = getMyMonome()
        if (monome.curPage == 0) {
            PatternBank patterns = monome.patternBanks.get(0)
            int length = patterns.patternLengths[patterns.curPattern]
            int endLed = (int) ((float) length / 96.0f * 16.0f)
            Integer[] levels = new Integer[64]
            for (int i = 0; i < 64; i++) {
                if (i <= endLed) {
                    levels[i] = 15
                } else {
                    levels[i] = 0
                }
            }
            map(0, levels)
        } else if (monome.curPage == 1) {
            int endLed = (int) ((float) swing * 8.0f)
            Integer[] levels = new Integer[64]
            for (int i = 0; i < 64; i++) {
                if (i <= endLed) {
                    levels[i] = 15
                } else {
                    levels[i] = 0
                }
            }
            map(0, levels)
        } else {
            all(0, 0)
        }
    }
    
    void drawCommandEnc1() {
        MonomeConfiguration monome = getMyMonome()
        if (monome.curPage == 0) {
            // get patterns for page 0
            PatternBank patterns = monome.patternBanks.get(0)
            int pos = patterns.patternPosition[patterns.curPattern]
            int length = patterns.patternLengths[patterns.curPattern]
            int endLed = (int) ((float) pos / (float) length * 64.0f)
            Integer[] levels = new Integer[64]
            for (int i = 0; i < 64; i++) {
                if (i <= endLed) {
                    levels[i] = 15
                } else {
                    levels[i] = 0
                }
            }
            map(1, levels)
        } else if (monome.curPage == 1) {
            int endLed = (int) ((float) length / 96.0f * 16.0f)
            Integer[] levels = new Integer[64]
            for (int i = 0; i < 64; i++) {
                if (i <= endLed) {
                    levels[i] = 15
                } else {
                    levels[i] = 0
                }
            }
            map(1, levels)
        } else {
            all(1, 0)
        }
    }

    void drawCommandEnc2() {
        MonomeConfiguration monome = getMyMonome()
        if (monome.curPage == 0) {
            int endLed = fingersVelo / 2
            Integer[] levels = new Integer[64]
            for (int i = 0; i < 64; i++) {
                if (i <= endLed) {
                    levels[i] = 15
                } else {
                    levels[i] = 0
                }
            }
            map(2, levels)
        } else if (monome.curPage == 1) {
            int endLed = stepVelo / 2
            Integer[] levels = new Integer[64]
            for (int i = 0; i < 64; i++) {
                if (i <= endLed) {
                    levels[i] = 15
                } else {
                    levels[i] = 0
                }
            }
            map(2, levels)
        } else {
            all(2, 0)
        }
    }

    void drawPageEnc() {
        MonomeConfiguration monome = getMyMonome()
        int numPages = monome.numPages
        int curPage = monome.curPage
        Integer[] levels = new Integer[64]
        for (int i = 0; i < 64; i++) {
            levels[i] = 0
        }
        for (int i = 0; i < numPages; i++) {
            int startLed = (int) ((float) i * 64.0f / (float) numPages)
            levels[startLed] = 15
            if (i == curPage) {
                int endLed = (int) ((float) (i + 1) * 64.0f / (float) numPages)
                for (int j = startLed; j < endLed; j++) {
                    levels[j] = 15
                }
            }
        }
        map(3, levels)
    }

    void note(int num, int velo, int chan, int on) {
    }

    void cc(int num, int val, int chan) {
    }

    void clock() {
        MonomeConfiguration monome = getMyMonome()
        if (monome.curPage == 0) {
            drawCommandEnc1()
        }
    }

    void clockReset() {
        redrawDevice()
    }

    MonomeConfiguration getMyMonome() {
        MonomeConfiguration monome = MonomeConfigurationFactory.getMonomeConfiguration(prefix)
    }

}
