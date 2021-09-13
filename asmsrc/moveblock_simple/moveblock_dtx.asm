            SRC EQU 128
            DST EQU 130
            LEN EQU 132
            
            OPT H-
            ORG 40000

MOVEBLOCK   PHA
            TYA
            PHA
            TXA
            PHA
               
            LDA #$FF
            STA SRC
            LDA #$FE
            STA SRC+1
            LDA #$EF
            STA DST
            LDA #$EE
            STA DST+1
            LDA #$DF
            STA LEN
            LDA #$FE
            STA LEN+1

_MOVFWD	    LDY #0		   ;Initialise the index
	        LDX LEN+1	   ;Load the page count
	        BEQ _FRAG	   ;... Do we only have a fragment?
_PAGE    	LDA (SRC),Y	   ;Move a byte in a page transfer
	        STA (DST),Y
	        INY		       ;And repeat for the rest of the
	        BNE _PAGE	   ;... page
	        INC SRC+1	   ;Then bump the src and dst addresses
	        INC DST+1      ;... by a page
	        DEX		       ;And repeat while there are more
	        BNE _PAGE	   ;... pages to move
_FRAG	    CPY LEN+0	   ;Then while the index has not reached
	        BEQ _DONE	   ;... the limit
	        LDA (SRC),Y	   ;Move a fragment byte
	        STA (DST),Y
	        INY		       ;Bump the index and repeat
	        BNE _FRAG
_DONE 	EQU *		       ;All done
            PLA
            TAX
            PLA
            TAY
            PLA
            RTS