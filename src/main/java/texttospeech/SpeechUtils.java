package texttospeech;

import java.beans.PropertyVetoException;
import java.util.Locale;

import javax.speech.AudioException;
import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

public class SpeechUtils extends Thread {

    private SynthesizerModeDesc desc;
    private Synthesizer synthesizer;
    private Voice voice;
    private String textToSpeak;

    public SpeechUtils(String textToSpeak) {
        super();
        this.textToSpeak = textToSpeak;
    }

    public static void main(String[] args) throws Exception {
        final String sample =
                "Starbucks Corporation is an American coffee company and coffeehouse chain. Starbucks was founded in Seattle, Washington in 1971. As of 2018, the company operates 28,218 locations worldwide.Starbucks is considered the main representative of \"second wave coffee\", initially distinguishing itself from other coffee-serving venues in the US by taste, quality, and customer experience while popularizing darkly roasted coffee. Since the 2000s, third wave coffee makers have targeted quality-minded coffee drinkers with hand-made coffee based on lighter roasts, while Starbucks nowadays uses automated espresso machines for efficiency and safety reasons.Starbucks locations serve hot and cold drinks, whole-bean coffee, microground instant coffee known as VIA, espresso, caffe latte, full- and loose-leaf teas including Teavana tea products, Evolution Fresh juices, Frappuccino beverages, La Boulange pastries, and snacks including items such as chips and crackers; some offerings (including their annual fall launch of the Pumpkin Spice Latte) are seasonal or specific to the locality of the store. Many stores sell pre-packaged food items, hot and cold sandwiches, and drinkware including mugs and tumblers; select \"Starbucks Evenings\" locations offer beer, wine, and appetizers. Starbucks-brand coffee, ice cream, and bottled cold coffee drinks are also sold at grocery stores.Starbucks first became profitable in Seattle in the early 1980s. Despite an initial economic downturn with its expansion into the Midwest and British Columbia in the late 1980s, the company experienced revitalized prosperity with its entry into California in the early 1990s. The first Starbucks location outside North America opened in Tokyo in 1996; overseas properties now constitute almost one-third of its stores. The company opened an average of two new locations daily between 1987 and 2007.On December 1, 2016, Howard Schultz announced he would resign as CEO effective April 2017 and would be replaced by Kevin Johnson. Johnson assumed the role of CEO on April 3, 2017, and Howard Schultz retired to become Chairman Emeritus effective June 26, 2018.";
        SpeechUtils su = new SpeechUtils(sample);
        su.speak();
        //su.start();
        System.out.println("end");
        //su = new SpeechUtils("hi nakul patil");
        // su.speak();
        // su.start();
        System.out.println("end");
    }

    public void doSpeak()
            throws EngineException, AudioException, IllegalArgumentException,
            InterruptedException {
        synthesizer.speakPlainText(textToSpeak, null);
        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
    }

    public void init(String voiceName)
            throws EngineException, AudioException, EngineStateError,
            PropertyVetoException {
        if (desc == null) {
            System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
            desc = new SynthesizerModeDesc(Locale.US);
            Central.registerEngineCentral("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
            synthesizer = Central.createSynthesizer(desc);
            synthesizer.allocate();
            synthesizer.resume();
            final SynthesizerModeDesc smd = (SynthesizerModeDesc) synthesizer.getEngineModeDesc();
            final Voice[] voices = smd.getVoices();
            Voice voice = null;
            for (final Voice voice2 : voices) {
                if (voice2.getName().equals(voiceName)) {
                    voice = voice2;
                    break;
                }
            }
            synthesizer.getSynthesizerProperties().setVoice(voice);
        }
    }

    @Override
    public void run() {
        try {
            Thread.currentThread().setName(textToSpeak);
            speak();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void speak() {
        try {
            init("kevin16");
            // high quality
            doSpeak();
            // terminate();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void terminate() throws EngineException, EngineStateError {
        synthesizer.deallocate();
    }

}