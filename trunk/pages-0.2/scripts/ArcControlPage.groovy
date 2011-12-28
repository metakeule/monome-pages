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
    def pattLengths = []
    int activePattern = 0

    int stepVelo = 127
    int swing = 0
    int swingTurn = 0
    int swingSens = 40
    int length = 96
    int lengthTurn = 0
    int lengthSens = 40
    
    int activeLooper = 0
    int loopLengthTurn = 0
    int loopLengthSens = 40
    def loopLength = []

    int cmd1Down = 0
    int cmd2Down = 0

    void init() {
        log("ArcControlPage starting up")
        for (int i = 0; i < 16; i++) {
            loopLength[i] = 192
            pattLengths[i] = 192
        }
    }

    void stop() {
        log("ArcControlPage shutting down")
    }

    void delta(int enc, int delta) {
        MonomeConfiguration monome = getMyMonome()
        if (enc == 0) {
            if (monome.curPage == 0) {
                livePageEnc0(monome, delta)
            } else if (monome.curPage == 1) {
                gridStepPageEnc0(monome, delta)
            } else if (monome.curPage == 2) {
                midiLoopPageEnc0(monome, delta)
            }
        }
        if (enc == 1) {
            if (monome.curPage == 0) {
                livePageEnc1(monome, delta)
            } else if (monome.curPage == 1) {
                gridStepPageEnc1(monome, delta)
            } else if (monome.curPage == 2) {
                midiLoopPageEnc1(monome, delta)
            }
        }
        if (enc == 2) {
            if (monome.curPage == 0) {
                livePageEnc2(monome, delta)
            } else if (monome.curPage == 1) {
                gridStepPageEnc2(monome, delta)
            } else if (monome.curPage == 2) {
                midiLoopPageEnc2(monome, delta)
            }
        }
        if (enc == 3) {
            handlePageChangeDelta(delta)
        }
    }

    void livePageEnc0(MonomeConfiguration monome, int delta) {
        pattLengths[activePattern] += delta
        PatternBank patterns = monome.patternBanks.get(0)
        int length = patterns.patternLengths[patterns.curPattern]
        if (pattLengths[activePattern] < -pattSens) {
            if (length > 96) {
                length -= 96
            }
            patterns.patternLengths[patterns.curPattern] = length
            pattLengths[activePattern] = pattSens
            redrawDevice()
        }
        if (pattLengths[activePattern] > pattSens) {
            if (length < 96 * 4) {
                length += 96
            }
            patterns.patternLengths[patterns.curPattern] = length
            pattLengths[activePattern] = -pattSens
            redrawDevice()
        }
    }
    
    void livePageEnc1(MonomeConfiguration monome, int delta) {
        PatternBank patterns = monome.patternBanks.get(0)
        if (cmd1Down == 0) {
            movePlayhead(patterns, delta, patterns.curPattern)
        } else {
            for (int patternNum = 0; patternNum < patterns.numPatterns; patternNum++) {
                movePlayhead(patterns, delta, patternNum)
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
        sendCommandToPage(new Command("offsetPattern", args))
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
    
    void livePageEnc2(MonomeConfiguration monome, int delta) {
        int newFingersVelo = fingersVelo  + delta
        if (newFingersVelo > 127) newFingersVelo = 127
        if (newFingersVelo < 0) newFingersVelo = 0
        if (newFingersVelo != fingersVelo) {
            fingersVelo = newFingersVelo
            Command cmd = new Command("velocity", fingersVelo)
            sendCommandToPage(cmd)
            drawLivePageEnc2(monome)
        }
    }

    void gridStepPageEnc0(MonomeConfiguration monome, int delta) {
        lengthTurn += delta
        if (lengthTurn < -lengthSens) {
            if (length > 96) {
                length -= 96
                lengthTurn = lengthSens
                sendCommandToPage(new Command("length", length))
                redrawDevice()
            }
        } else if (lengthTurn > lengthSens) {
            if (length < 96 * 4) {
                length += 96
                lengthTurn = -lengthSens
                sendCommandToPage(new Command("length", length))
                redrawDevice()
            }
        }
    }

    void gridStepPageEnc1(MonomeConfiguration monome, int delta) {
        swingTurn += delta
        if (swingTurn < -swingSens) {
            if (swing > 0) {
                swing--
                sendCommandToPage(new Command("swing", swing))
                swingTurn = swingSens
                redrawDevice()
            }
        } else if (swingTurn > swingSens) {
            if (swing < 8) {
                swing++
                sendCommandToPage(new Command("swing", swing))
                swingTurn = -swingSens
                redrawDevice()
            }
        }
    }

    void gridStepPageEnc2(MonomeConfiguration monome, int delta) {
        int newStepVelo = stepVelo  + delta
        if (newStepVelo > 127) newStepVelo = 127
        if (newStepVelo < 0) newStepVelo = 0
        if (newStepVelo != stepVelo) {
            stepVelo = newStepVelo
            Command cmd = new Command("velocity", stepVelo)
            sendCommandToPage(cmd)
            drawGridStepPageEnc2(monome)
        }
    }

    void midiLoopPageEnc0(MonomeConfiguration monome, int delta) {
        loopLengthTurn += delta
        if (loopLengthTurn < -loopLengthSens) {
            if (loopLength[(int) (activeLooper / 2)] > 192) {
                loopLength[(int) (activeLooper / 2)] -= 192
                loopLengthTurn = loopLengthSens
                sendCommandToPage(new Command("length", loopLength[(int) (activeLooper / 2)]))
                redrawDevice()
            }
        } else if (loopLengthTurn > loopLengthSens) {
            if (loopLength[(int) (activeLooper / 2)] < 192 * 4) {
                loopLength[(int) (activeLooper / 2)] += 192
                loopLengthTurn = -loopLengthSens
                sendCommandToPage(new Command("length", loopLength[(int) (activeLooper / 2)]))
                redrawDevice()
            }
        }
    }
    
    void midiLoopPageEnc1(MonomeConfiguration monome, int delta) {        
    }
    
    void midiLoopPageEnc2(MonomeConfiguration monome, int delta) {
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
    
    void sendCommandToPage(Command command) {
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
                    sendCommandToPage(new Command("resetPlayhead", null))
                }
            }
            cmd1Down = key
        }
        redrawDevice()
    }

    void redrawDevice() {
        MonomeConfiguration monome = getMyMonome()
        if (monome.curPage == 0) {
            drawLivePageEnc0(monome);
            drawLivePageEnc1(monome);
            drawLivePageEnc2(monome);
        }
        if (monome.curPage == 1) {
            drawGridStepPageEnc0(monome);
            drawGridStepPageEnc1(monome);
            drawGridStepPageEnc2(monome);
        }
        if (monome.curPage == 2) {
            drawMidiLoopPageEnc0(monome);
            drawMidiLoopPageEnc1(monome);
            drawMidiLoopPageEnc2(monome);
        }
        drawPageEnc();
    }
    
    void drawLivePageEnc0(MonomeConfiguration monome) {        
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
    }
            
    void drawLivePageEnc1(MonomeConfiguration monome) {
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
    }
    
    void drawLivePageEnc2(MonomeConfiguration monome) {
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
    }
    
    void drawGridStepPageEnc0(MonomeConfiguration monome) {
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
    }

    void drawGridStepPageEnc1(MonomeConfiguration monome) {
        int endLed = (int) ((float) swing * 8.0f)
        Integer[] levels = new Integer[64]
        for (int i = 0; i < 64; i++) {
            if (i <= endLed) {
                levels[i] = 15
            } else {
                levels[i] = 0
            }
        }
        map(1, levels)
    }

    void drawGridStepPageEnc2(MonomeConfiguration monome) {
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
    }
    
    void drawMidiLoopPageEnc0(MonomeConfiguration monome) {
        int endLed = (int) ((float) loopLength[(int) (activeLooper / 2)] / 192.0f * 16.0f)
        Integer[] levels = new Integer[64]
        for (int i = 0; i < 64; i++) {
            if (i <= endLed) {
                levels[i] = 15
            } else {
                levels[i] = 0
            }
        }
        map(0, levels)
    }
    
    void drawMidiLoopPageEnc1(MonomeConfiguration monome) {
        all(1, 0)
    }
    void drawMidiLoopPageEnc2(MonomeConfiguration monome) {
        all(2, 0)
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

    void clock() {
        MonomeConfiguration monome = getMyMonome()
        if (monome.curPage == 0) {
            drawLivePageEnc1(monome)
        }
    }

    void clockReset() {
        redrawDevice()
    }
    
    void sendCommand(Command cmd) {
        if (cmd.getCmd().equalsIgnoreCase("activeLooper")) {
            activeLooper = (Integer) cmd.getParam()
            MonomeConfiguration monome = getMyMonome()
            if (monome.curPage == 2) {
                drawMidiLoopPageEnc0(monome)
            }
        }
        if (cmd.getCmd().equalsIgnoreCase("activePattern")) {
            log("got msg")
            activePattern = (Integer) cmd.getParam()
            MonomeConfiguration monome = getMyMonome()
            if (monome.curPage == 0) {
                drawLivePageEnc0(monome)
            }
        }
    }

    MonomeConfiguration getMyMonome() {
        MonomeConfiguration monome = MonomeConfigurationFactory.getMonomeConfiguration(prefix)
        return monome
    }

}
