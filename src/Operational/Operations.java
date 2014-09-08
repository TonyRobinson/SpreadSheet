package Operational;
import java.util.ArrayList;
import java.util.Stack;


/** 
 * This is a helper (utility) class for operations in spread sheet application.
 * @author 
 *
 */
public class Operations {
	private Stack<Character> stack;
	
	private ArrayList<String> output;

	/**
	 * 
	 */
	public Operations() {
		stack = new Stack<Character>();
		output = new ArrayList<String>();
	}

	/**
	 * 
	 * @param in
	 * @return
	 */
	private String cleanInput(String in) {
		String clean = "";
		if(in.length() > 0 && in.charAt(0) == '-'){
			String s = "0";
			s += in;
			in = s;			
		}
		for(int i = 0; i<in.length(); i++){
			String curr = in.substring(i, i+1);
			if(!curr.equals(" ")){
				clean += curr;
			}
		}
		return clean;
	}
    	
	/**
	 *  Converts the infix expression into the postfix
	 *  and returns the arrayList of Strings.
	 *  
	 *  @param String input
	 *  @retrun ArrayList<String>
	 */
	public ArrayList<String> infixToPostfix(String input) {
		input = cleanInput(input);
		for (int j = 0; j < input.length(); j++) {
			char ch = input.charAt(j);
			switch (ch) {
			case '+':
			case '-':
				checkOperator(ch, 1);
				break;
			case '*':
			case '/':
				checkOperator(ch, 2);
				break;
			case '(':
				stack.push(ch);
				break;
			case ')':
				hasParentheses(ch); // go pop operators
				break;
			default:
				String tmp = "";
				if (j < input.length() - 1) {
					while (isValid(ch)) {
						tmp += ch;
						if(j + 1 < input.length())
							ch = input.charAt(++j);
						else {
							j++;
							break;
						}
					}
					j--;
				} else {
					tmp += ch;
				}
				output.add(tmp);
				break;
			}
		}
		while (!stack.isEmpty()) {
			output.add(String.valueOf(stack.pop()));

		}
		return output; 
	}

	
	/**
	 * Chesks if the character passed is a valid digit.
	 * @param the_digit
	 * @return boolean true if digit is valid
	 * 
	 */
	private boolean isValid(char the_digit) {
		boolean isDigit = true;
		char ch = the_digit;
		if ((ch == '+') || (ch == '-') || (ch == '/') || (ch == '*') || (ch == ')') || (ch == '(')) {
			isDigit = false;
		}
		return isDigit;
	}

	/**
	 * 
	 * @param operator
	 * @param prec1
	 */
	private void checkOperator(char operator, int prec1) {
		while (!stack.isEmpty()) {
			char currChar = stack.pop();
			if (currChar == '(') {
				stack.push(currChar);
				break;
			} else {
				int prec2;
				if (currChar == '+' || currChar == '-')
					prec2 = 1;
				else
					prec2 = 2;
				if (prec2 < prec1)
				{ 
					stack.push(currChar);
					break;
				} else
					output.add(String.valueOf(currChar));
			}
		}
		stack.push(operator);
	}

	/**
	 * Checks if the the Stack of characters contains a parentheses if so
	 * adds that into the arrayList string output.  
	 * 
	 * @param ch ??
	 */
	private void hasParentheses(char ch) {
		while (!stack.isEmpty()) {
			char chx = stack.pop();
			if (chx == '(')
				break;
			else
				output.add(String.valueOf(chx));
		}
	}

	/**
	 *  Calculates the value of the postfix expression 
	 *  
	 */
	public double calculatePostfix(ArrayList<String> tokens) throws NumberFormatException {
		Stack<Double> sk = new Stack<Double>();
		for (String currToken : tokens) {
			if (currToken.equals("+")) {
				sk.push(sk.pop() + sk.pop());
			} else if (currToken.equals("-")) {
				Double arg2 = sk.pop();
				sk.push(sk.pop() - arg2);
			} else if (currToken.equals("*")) {
				sk.push(sk.pop() * sk.pop());
			} else if (currToken.equals("/")) {
				Double arg2 = sk.pop();
				sk.push(sk.pop() / arg2);
			} else {
				sk.push(Double.parseDouble(currToken));
			}
		}
		return sk.pop();
	}
	
	/**
	 * Returns true if the String s is a cell.
	 */
	public static boolean isCell(String s) {
		boolean isCell = false;
			if (!s.equals("+") && !s.equals("/") && !s.equals("*")
					&& !s.equals("-")) {
				try {
					Double.parseDouble(s);
				} catch (NumberFormatException n) {
					//Internal error, no message needed
					isCell = true;
				}
			}
		return isCell;
	}
	
	
	public static void main(String[] args){
		String input = "A2+(A3-A4) * 5";
		Operations theTrans = new Operations();
		ArrayList<String>  aoutput = theTrans.infixToPostfix(input);
		System.out.println(aoutput);
	}

}