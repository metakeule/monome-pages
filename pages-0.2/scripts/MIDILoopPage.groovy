import org.monome.pages.api.GroovyAPI

class LiveLoopPage extends GroovyAPI {

    def buffers = []
    def oldBuffers = []
    int activeBuffer = -1
    int tickNum = -1

    void init() {
        log("LiveLoopPage starting up")
        for (int i = 0; i < sizeX(); i++) {
            buffers[i] = new MIDIBuffer()
        }
        redraw()
    }

    void stop() {
        log("LiveLoopPage shutting down")
    }

    void press(int x, int y, int val) {
        if (val == 1) {
            if (y > sizeY() - 5 && y < sizeY() - 1) {
                if (oldBuffers[x] != null && oldBuffers[x][y - sizeY() + 4] != null) {
                    for (int i = 0; i < buffers[activeBuffer].playingNotes.size(); i++) {
                        MIDINote note = buffers[activeBuffer].playingNotes[i]
                        noteOut(note.note, note.velocity, activeBuffer, 0)
                    }
                    buffers[activeBuffer].playingNotes = []
                    MIDIBuffer tmpBuffer = buffers[activeBuffer]
                    buffers[activeBuffer] = oldBuffers[x][2 - (y - sizeY() + 4)]
                    oldBuffers[x][2 - (y - sizeY() + 4)] = tmpBuffer
                }
            }
            if (y == sizeY() - 1) {
                activateBuffer(x)
                redraw()
            }
        }
    }

    void activateBuffer(int buf) {
        if (activeBuffer == buf) {
            for (int i = 0; i < buffers[activeBuffer].playingNotes.size(); i++) {
                MIDINote note = buffers[activeBuffer].playingNotes[i]
                noteOut(note.note, note.velocity, activeBuffer, 0)
            }
            buffers[activeBuffer].playingNotes = []
            if (oldBuffers[activeBuffer] == null) {
                oldBuffers[activeBuffer] = []
            }
            oldBuffers[activeBuffer][2] = oldBuffers[activeBuffer][1]
            oldBuffers[activeBuffer][1] = oldBuffers[activeBuffer][0]
            oldBuffers[activeBuffer][0] = buffers[activeBuffer]
            buffers[activeBuffer] = new MIDIBuffer()
        }
        buffers[activeBuffer].recording = 0
        buffers[buf].recording = 1
        activeBuffer = buf
    }

    void redraw() {
        // old buffers
        for (int x = 0; x < sizeX(); x++) {
            for (int i = 0; i < 3; i++) {
                if (oldBuffers[x] != null && oldBuffers[x][i] != null && oldBuffers[x][i].hasNotes) {
                    led(x, sizeY() - 2 - i, 1)
                } else {
                    led(x, sizeY() - 2 - i, 0)
                }
            }
        }
        // bottom row
        for (int x = 0; x < sizeX(); x++) {
            if (buffers[x].recording) continue
            if (buffers[x].hasNotes) {
                led(x, sizeY() - 1, 1)
            } else {
                led(x, sizeY() - 1, 0)
            }
        }
    }

    void note(int num, int velo, int chan, int on) {
        if (velo < 40) velo = 40
        buffers[activeBuffer].setNote(tickNum % buffers[activeBuffer].length, new MIDINote(num, velo, on))
        noteOut(num, velo, activeBuffer, on)
        redraw()
    }

    void cc(int num, int val, int chan) {
    }

    void clock() {
        tickNum++
        // max length
        if (tickNum == 96*8) {
            tickNum = 0
        }
        for (int x = 0; x < sizeX(); x++) {
            if (buffers[x] == null) continue
            if (buffers[x].recording) {
                if (tickNum % 24 < 12) {
                    led(x, sizeY() - 1, 1)
                } else {
                    led(x, sizeY() - 1, 0)
                }
            }
            def notes = buffers[x].notes[tickNum % buffers[x].length]
            if (notes != null) {
                for (int i = 0; i < notes.size(); i++) {
                    noteOut(notes[i].note, notes[i].velocity, x, notes[i].state)
                    if (notes[i].state == 1) {
                        buffers[x].playingNotes.push(notes[i])
                    } else {
                        for (int j = 0; j < buffers[x].playingNotes.size(); j++) {
                            if (buffers[x].playingNotes[j].note == notes[i].note) {
                                buffers[x].playingNotes -= buffers[x].playingNotes[j]
                            }
                        }
                    }
                }
            }
        }
    }

    void clockReset() {
        tickNum = -1
    }

    class MIDIBuffer {
        public int length = 96
        public int recording = 0
        public int hasNotes = 0
        public notes = []
        public playingNotes = []
        
        void setNote(int position, MIDINote note) {
            if (notes[position] == null) {
                notes[position] = []
            }
            notes[position].push(note)
            hasNotes = 1
        }

        void clear() {
            notes = []
            hasNotes = 0
        }
    }
    
    class MIDINote {
        public int note;
        public int velocity;
        public int state;
        public MIDINote(int note, int velocity, int state) {
            this.note = note
            this.velocity = velocity
            this.state = state
        }
    }
}