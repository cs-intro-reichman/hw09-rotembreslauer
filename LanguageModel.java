import java.util.HashMap;
import java.util.Random;

public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given window length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
		// Your code goes here
        String window = "";
        char c;
        In in = new In(fileName);
        for(int i = 0;i < windowLength; i ++){
            c = in.readChar(); 
            window += c;
        }
        while (!in.isEmpty()){
            c = in.readChar();
            if (CharDataMap.containsKey(window))
            {
                CharDataMap.get(window).update(c);
            }
            else
            {
                List probs = new List();
                probs.addFirst(c);
                CharDataMap.put(window, probs);
            }
            window = window.substring(1) + c;
        }

        for (List probs : CharDataMap.values())
        calculateProbabilities(probs);
	
          
        }

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	public void calculateProbabilities(List probs) {				
		// Your code goes here
        int countCharInList = 0;
        Node current = probs.getFirstNode();
        while (current != null)
        {
            countCharInList += current.cp.count;
            current = current.next;
        }

        double currentCP = 0;
        current = probs.getFirstNode();
        while (current != null)
        {
            current.cp.p = (double) current.cp.count / countCharInList;
            currentCP += (double) current.cp.count / countCharInList;;
            current.cp.cp = currentCP;
            current = current.next;
        }
	}

    // Returns a random character from the given probabilities list.
	public char getRandomChar(List probs) {
		// Your code goes here
            calculateProbabilities(probs);
            double random = randomGenerator.nextDouble();
            ListIterator it = probs.listIterator(0);
            Node first = probs.getFirstNode();
            if (first.cp.p>random){
                return first.cp.chr;
            }
            while (it.hasNext()){
                CharData current = it.next();
                if (random<=current.cp){
                    return current.chr;
                }
            }
            return ' ';
        }
    
    
    }

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
		// Your code goes here
        String window = "";
        String text = initialText;
        char c;

        if (windowLength > initialText.length() || initialText.length() >= textLength){
            return initialText;
        }else{
            window = initialText.substring(initialText.length() - windowLength);
            while (text.length() - windowLength < textLength) {
                if (CharDataMap.containsKey(window)){
                    c = getRandomChar(CharDataMap.get(window));
                    text += c;
                    window = window.substring(1) + c;
                }else{
                    return text;
                }
            }
            return text;
        }
    }
    
    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		// Your code goes here
    }
}
