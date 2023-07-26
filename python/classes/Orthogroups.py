
class Orthogroups:
    """Orthogroup objects are used to identify an orthogroup based on a single protein within it"""

    def __init__(self, group: list):
        """Convert the list of orthogroup elements into a set for better lookup"""
        self.group = set(group)

    def find_protein(self, prot):
        """find_protein() takes a protein and checks if it exists in the group. If it does return everything else in the list
        otherwise return False
        
        :param prot: name of protein as str
        :return: list of other proteins in group if exists else return False"""
        val = False
        if prot in self.group:
            val = self.group
        return val
    
    def __eq__(self, o: object) -> bool:
        if not isinstance(o, Orthogroups):
            return False
        return self.group == o.group
    
    def __hash__(self) -> int:
        return hash(self.group)