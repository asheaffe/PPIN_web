"""ortho_processing.py: takes data from inParanoid and processes it for use on the FE"""
__author__ = "Anna Sheaffer"
__email__ = "asheaffe@iwu.edu"
__date__ = "May 22, 2023"

from classes import Orthologies
from classes import Orthology

def main():
    filepath = "./mouse_worm_inparanoid.fa"

    with open(filepath, 'r') as f1:
        content = f1.readlines()

        line1 = content[0].split('\t')

        ortho_pair = Orthologies.Orthologies
        ortho_pair.instance(line1)

        ortho = Orthology.Orthology
        ortho.instance(line1)
        for line in content[1:]:
            line = line.split('\t')
            ortho_pair.add_protein(line)
            ortho.instance(line)

    print(ortho.instance(None))
    # file = open("test_orthology_data.txt", "w")
    # file.write(str(ortho_pair.instance([])))
main()