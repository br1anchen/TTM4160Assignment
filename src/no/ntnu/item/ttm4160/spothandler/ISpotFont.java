package no.ntnu.item.ttm4160.spothandler;

/**
 * This is the interface to an 8 pixel tall font defined in columns so
 * that it can be displayed by the Sun SPOT as it is waved around in
 * the air.
 * 
 * @author rogermeike
 */
public interface ISpotFont {
    /**
     * Given a character, get the numberic values that correspond to
     * it.
     * @param character The character to look up
     * @return An array of ints where each int is an 8-bit column of 
     * on/off values for each LED on the eDemoBoard. Together the 
     * array columns should define a bitmap for the character in question.
     */
    int[] getChar(char character);
    
    /**
     * Return the width in pixels of the character in question
     * @param character The character to examine
     * @return The width in pixels
     */
    int getCharWidth(char character);
}
