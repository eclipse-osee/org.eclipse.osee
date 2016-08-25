/*
 * from Xtext - Xbase
 */
package org.eclipse.osee.orcs.script.dsl.typesystem;

import static org.eclipse.xtext.util.Strings.equal;
import com.google.inject.Singleton;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Based on XBase number literals
 */
@Singleton
public class NumberConverter {

   public int getBase(String literal) {
      if (isHex(literal)) {
         return 16;
      } else {
         return 10;
      }
   }

   protected String getTypeQualifier(String literal) {
      String valueAsLowerCase = literal.toLowerCase();
      switch (getBase(literal)) {
         case 16:
            int index = valueAsLowerCase.indexOf('#');
            if (index != -1) {
               return valueAsLowerCase.substring(index + 1);
            } else {
               return "";
            }
         case 10:
            if (valueAsLowerCase.endsWith("bi") || valueAsLowerCase.endsWith("bd")) {
               return valueAsLowerCase.substring(valueAsLowerCase.length() - 2);
            }
            char lastChar = valueAsLowerCase.charAt(literal.length() - 1);
            switch (lastChar) {
               case 'l':
               case 'd':
               case 'f':
                  return Character.toString(lastChar);
               default:
                  return "";
            }
         default:
            throw new IllegalArgumentException("Invalid number literal base " + literal);
      }
   }

   public String toJavaLiteral(String literal) {
      if (getJavaType(literal).isPrimitive()) {
         return literal.replace("_", "").replace("#", "");
      } else {
         return null;
      }
   }

   public String getDigits(String literal) {
      return getXbaseDigits(literal).replace("_", "");
   }

   protected String getXbaseDigits(String literal) {
      int length = literal.length();
      String typeQualifier = getTypeQualifier(literal);
      switch (getBase(literal)) {
         case 10:
            return literal.substring(0, length - typeQualifier.length());
         case 16:
            if (equal("", typeQualifier)) {
               return literal.substring(2, length - typeQualifier.length());
            } else {
               return literal.substring(2, length - typeQualifier.length() - 1);
            }
         default:
            throw new IllegalArgumentException("Invalid number literal base " + getBase(literal));
      }
   }

   protected boolean isFloatingPoint(String literal) {
      if (literal.indexOf('.') != -1) {
         return true;
      }
      String lowerCaseValue = literal.toLowerCase();
      switch (getBase(literal)) {
         case 16:
            return false;
         case 10:
            if (lowerCaseValue.indexOf('e') != -1) {
               return true;
            }
            char lastChar = lowerCaseValue.charAt(literal.length() - 1);
            return lastChar == 'd' || lastChar == 'f';
         default:
            throw new IllegalArgumentException("Invalid number literal base " + getBase(literal));
      }
   }

   protected Class<? extends Number> getExplicitJavaType(String literal) {
      String typeQualifier = getTypeQualifier(literal);
      if (equal("", typeQualifier)) {
         return null;
      } else if (equal("f", typeQualifier)) {
         return Float.TYPE;
      } else if (equal("l", typeQualifier)) {
         return Long.TYPE;
      } else if (equal("d", typeQualifier)) {
         return Double.TYPE;
      } else if (equal("bi", typeQualifier)) {
         return BigInteger.class;
      } else if (equal("bd", typeQualifier)) {
         return BigDecimal.class;
      } else {
         throw new IllegalArgumentException("Invalid type qualifier " + typeQualifier);
      }
   }

   public Class<? extends Number> getJavaType(String literal) {
      Class<? extends Number> explicitType = getExplicitJavaType(literal);
      return explicitType == null ? isFloatingPoint(literal) ? Double.TYPE : Integer.TYPE : explicitType;
   }

   public Number numberValue(String literal, Class<?> numberType) {
      if (numberType == Integer.TYPE || numberType == Integer.class) {
         BigInteger asBigInt = toBigInteger(literal);
         BigInteger shiftRight = asBigInt.shiftRight(32);
         if (shiftRight.getLowestSetBit() != -1 || asBigInt.testBit(31) && getBase(literal) == 10) {
            throw new NumberFormatException("Integer literal is out of range: " + literal);
         }
         return asBigInt.intValue();
      } else if (numberType == Double.TYPE || numberType == Double.class) {
         return Double.parseDouble(getDigits(literal));
      } else if (numberType == Long.TYPE || numberType == Long.class) {
         BigInteger asBigInt = toBigInteger(literal);
         BigInteger shiftRight = asBigInt.shiftRight(64);
         if (shiftRight.getLowestSetBit() != -1 || asBigInt.testBit(63) && getBase(literal) == 10) {
            throw new NumberFormatException("Long literal is out of range: " + literal);
         }
         return asBigInt.longValue();
      } else if (numberType == Float.TYPE || numberType == Float.class) {
         return Float.parseFloat(getDigits(literal));
      } else if (numberType == BigInteger.class) {
         return toBigInteger(literal);
      } else if (numberType == BigDecimal.class) {
         return toBigDecimal(literal);
      } else {
         throw new IllegalArgumentException("Cannot convert number literal to type " + numberType.getCanonicalName());
      }
   }

   public BigInteger toBigInteger(String literal) {
      if (isFloatingPoint(literal)) {
         return toBigDecimal(literal).toBigInteger();
      } else {
         return new BigInteger(getDigits(literal), getBase(literal));
      }
   }

   public String getExponent(String literal, String digits) {
      if (isHex(literal)) {
         return null;
      }
      int e = digits.indexOf('e');
      if (e == -1) {
         e = digits.indexOf('E');
      }
      if (e != -1) {
         if (e != digits.length() - 1 && (digits.charAt(e + 1) == '+' || digits.charAt(e + 1) == '-')) {
            e++;
         }
         if (e < digits.length() - 1) {
            String exponent = digits.substring(e + 1);
            return exponent;
         }
      }
      return null;
   }

   protected boolean isHex(String literal) {
      if (literal.length() >= 2) {
         char second = literal.charAt(1);
         return literal.charAt(0) == '0' && (second == 'X' || second == 'x');
      }
      return false;
   }

   public BigDecimal toBigDecimal(String literal) {
      if (isFloatingPoint(literal)) {
         String digits = getDigits(literal);
         String exponent = getExponent(literal, digits);
         if (exponent != null && exponent.length() > 10) {
            throw new NumberFormatException("Too many nonzero exponent digits.");
         }
         return new BigDecimal(digits);
      } else {
         int base = getBase(literal);
         switch (base) {
            case 16:
               return new BigDecimal(new BigInteger(getDigits(literal), base));
            case 10:
               return new BigDecimal(getDigits(literal));
            default:
               throw new IllegalArgumentException("Invalid number literal base " + base);
         }
      }
   }
}
