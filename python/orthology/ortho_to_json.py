"""
	ortho_to_json.py: takes edges data file populated in PPI-Network-Alignment and converts it to JSON
"""

__author__ = "Anna Sheaffer"
__date__ = "June 14, 2023"

import json
from classes import Orthogroups

def main():
	# Filepath references orthology data files from PPI-Network-Alignment
	filepath = "../../../PPI-Network-Alignment/orthology-data/orthogroups/c_elegans-s_cerevisiae_orthogroups.txt"
	
	# list of orthogroup objects
	orthogroups = []
	with open(filepath, 'r') as file:
		for line in file:
			if line[0] == '!':
				file.readline()
			else: 
				group = line.strip().split("\t")
				orthogroups.append(Orthogroups.Orthogroups(group))

main()