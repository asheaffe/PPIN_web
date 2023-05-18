
"""
Class that maps all protein id's to one another
"""

import json
class Protein:

    def __init__(self, ids):
        self.gene_sid = ids[0]
        self.t_sid = ids[1]
        self.p_sid = ids[2]
        self.ncbi = ids[3]
        self.swissprot = ids[4]
        self.trembl = ids[5]
        self.name = ids[6].strip()

    def get_gene_sid(self):
        if self.gene_sid == "":
            return None
        return self.gene_sid

    def get_t_sid(self):
        if self.gene_sid == "":
            return None
        return self.t_sid

    def get_p_sid(self):
        if self.gene_sid == "":
            return None
        return self.p_sid

    def get_ncbi(self):
        if self.gene_sid == "":
            return None
        return self.ncbi

    def get_swissprot(self):
        if self.gene_sid == "":
            return None
        return self.swissprot

    def get_trembl(self):
        if self.gene_sid == "":
            return None
        return self.trembl

    def get_name(self):
        if self.gene_sid == "":
            return None
        return self.name

    # d = {x.get_name(): x for x in list_of_mappings}
    # d['ITSN-1'].get_swissprot()


if __name__ == '__main__':
    p = Protein(['1', '2', '3', '4', '5', '6', '7'])
    print(p)
    print(p.__dict__)