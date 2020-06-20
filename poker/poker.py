import numpy as np
from itertools import product
from random import sample
import sys
import re

class Game():
    def __init__(self, buy_in):
        self._balance = int(buy_in)
        self._blind = int(buy_in / 100)
        self._CARDS = list(product(['A','2','3','4','5','6','7','8','9','10','J','Q','K'],
                                   ['\u2666', '\u2665', '\u2660', '\u2663']))
        # self._CARDS = list(product(['10','J','Q','K'],
        #                            ['\u2666', '\u2665', '\u2660', '\u2663']))
        self.pay_table = {
            "ROYAL FLUSH": 800,
            "STRAIGHT FLUSH": 50,
            "FOUR OF A KIND": 25,
            "FULL HOUSE": 9,
            "FLUSH": 6,
            "STRAIGHT": 4,
            "THREE OF A KIND": 3,
            "TWO PAIR": 2,
            "JACKS OR BETTER": 1,
            "LOSE": 0
        }
        self.val_table = {
            '2': 2,
            '3': 3,
            '4': 4,
            '5': 5,
            '6': 6,
            '7': 7,
            '8': 8,
            '9': 9,
            '10': 10,
            'J': 11,
            'Q': 12,
            'K': 13,
            'A': 14
        }
        self.deck = self._CARDS

    def start(self):

        self.status = True
        while self.status:
            print("\nChips: " + str(self._balance))
            # Exit if 'exit' is typed
            if not self.show_options():
                break
            print()
            self.draw_init()
            self.get_hold()
            self.check_hand()

    def show_options(self):
        valid = False
        while not valid:
            print()
            choice = input("Enter amount to bet, \"exit\" to quit, or \"table\" for payout table: ")
            if choice == "exit":
                self.status = False
                valid = True
            elif choice == "table":
                print()
                print('\n'.join("{}: {}".format(k, v) for k, v in self.pay_table.items()))
                print()
                valid = False
            elif choice.isdigit():
                self._bet = int(choice)
                if self._balance - self._bet < 0:
                    print("Bet too high")
                    valid = False
                else:
                    self._balance = self._balance - self._bet
                    valid = True
            else:
                print("INVALID INPUT")
                valid = False

        return self.status

    def check_hand(self):
        # self._hand = [('2', '\u2666'), ('A', '♠'), ('A', '♠'), ('5', '♠'), ('4', '♠')]
        values = [self.val_table[card[0]] for card in self._hand]
        values.sort()

        suits = [card[1] for card in self._hand]
        val_count = {}
        for card in set(values):
            val_count.update({card: values.count(card)})
        suit_count = {}
        for card in set(suits):
            suit_count.update({card: suits.count(card)})

        # Check flush
        if 5 in suit_count:
            flush = True
        else:
            flush = False
        royal_straight = False

        if self.val_table['A'] in values:
            a_low = [1] + values[:-1]
            # Check for ace low straight
            if (a_low[4] == a_low[3] + 1 and a_low[3] == a_low[2] + 1 and
                a_low[2] == a_low[1] + 1 and a_low[1] == a_low[0] + 1):
                straight = True
            else:
                straight = False

        # Check straight
        if (values[4] == values[3] + 1 and values[3] == values[2] + 1 and
            values[2] == values[1] + 1 and values[1] == values[0] + 1):
            # Check for ace high straight
            if values[4] == self.val_table['A']:
                royal_straight = True
            straight = True
        else:
            straight = False

        print()
        if flush and royal_straight:
            self.update_balance("ROYAL FLUSH")
        elif flush and straight:
            self.update_balance("STRAIGHT FLUSH")
        elif 4 in val_count.values():
            self.update_balance("FOUR OF A KIND")
        elif 3 in val_count.values() and 2 in val_count.values():
            self.update_balance("FULL HOUSE")
        elif flush:
            self.update_balance("FLUSH")
        elif straight:
            self.update_balance("STRAIGHT")
        elif 3 in val_count.values():
            self.update_balance("THREE OF A KIND")
        elif list(val_count.values()).count(2) == 2:
            self.update_balance("TWO PAIR")
        elif 2 in val_count.values():
            jacks_or_better = False
            for val, count in val_count.items():
                if count == 2 and val >= self.val_table['J']:
                    jacks_or_better = True
            if jacks_or_better:
                self.update_balance("JACKS OR BETTER")
            else:
                self.update_balance("LOSE")
        else:
            self.update_balance("LOSE")
        print("__________________________________")
        pass


    def update_balance(self, result):
        self._balance = self._balance + self._bet * self.pay_table[result]
        print(result + "\n\n")
        if self._balance <= 0:
            print("OUT OF CHIPS")
            self.status = False

    def get_hold(self):
        valid = False
        while not valid:
            # hold = '1.3.4'
            # Check if hold inputs are valid
            hold = input("\nChoose cards to hold: ")
            print()
            if re.search('[a-zA-Z]', hold):
                valid = False
                print("INVALID INPUT")
                self.disp_hand()
            elif hold.isdigit():
                hold = [int(i) for i in hold]
                valid = True
            else:
                hold = re.findall(r'\d+', hold)
                hold = [int(i) for i in hold]
                valid = True

            if not (len(hold) == len(set(hold)) and all(i >= 1 and i <= 5 for i in hold)):
                valid = False
                print("INVALID INPUT")
                self.disp_hand()

        # Get difference between the hold cards and all cards
        diff = set([0, 1, 2, 3, 4]) - set([x - 1 for x in hold])

        # Replace non hold cards with a new card
        for i in diff:
            self._hand[i] = self.draw()
        self.disp_hand()

    def draw(self):
        return self.deck.pop()

    def draw_init(self):
        self.deck = sample(self._CARDS, len(self._CARDS))
        hand = []
        while len(hand) < 5:
            hand.append(self.draw())

        self._hand = hand
        self.disp_hand()

    def disp_hand(self, show_choice=True):
        hand_disp = []
        for card in self._hand:
            hand_disp.append(" ".join(card))

        disp = "|"
        choice = " [1]   [2]   [3]   [4]   [5] "
        for card in hand_disp:
            disp = disp + card + "| |"

        disp = disp[:-2]
        line = "-----------------------------"
        for x in range(disp.count("10")):
            line = line + "-"

        idx = [m.start() for m in re.finditer('10', disp)]
        for i in idx:
            choice = choice[:(i+3)] + " " + choice[(i+3):]
        print(line)
        print(disp)
        print(line)
        if show_choice:
            print(choice)
        pass

print("\nWELCOME TO JACKS OR HIGHER VIDEO POKER")
print("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
start = input("STARTING CHIP AMOUNT: ")
print("--------------------------------------")
game = Game(int(start))
game.start()