"""
	ortho_to_json.py: takes edges data file populated in PPI-Network-Alignment and converts it to JSON
"""

__author__ = "Anna Sheaffer"
__date__ = "June 14, 2023"

import json

def main():
	filepath = "mouse_worm_orthology_data.txt"

	with open(filepath, 'r') as file:
		#file.readline()
		ortho_groups = json.load(file)

main()