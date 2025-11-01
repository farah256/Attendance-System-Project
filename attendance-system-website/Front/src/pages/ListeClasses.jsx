import React, { useEffect, useState } from "react";
import axios from "axios";
import Layout from "../components/Layout";
import { Edit2, Trash2, Users2, Save } from "lucide-react";

function ListeClasses() {
    const [classes, setClasses] = useState([]);
    const [search, setSearch] = useState("");
    const [editId, setEditId] = useState(null);
    const [editNom, setEditNom] = useState("");
    const [editModule, setEditModule] = useState("");
    const [editAnneeScolaire, setEditAnneeScolaire] = useState("");

    const [newClasse, setNewClasse] = useState({
        nom: "",
        anneeScolaire: "",
        module: "",
        professeurId: localStorage.getItem("professeurId"),
        etudiants: []
    });
    const [isAdding, setIsAdding] = useState(false);

    useEffect(() => {
        const fetchClasses = async () => {
            const profId = localStorage.getItem("professeurId");
            if (profId) {
                try {
                    const res = await axios.get(`http://localhost:8080/api/classes/professeur/${profId}`);
                    setClasses(res.data);
                } catch (err) {
                    console.error(err);
                }
            }
        };
        fetchClasses();
    }, []);

    const handleDelete = async (id) => {
        if (!window.confirm("Voulez-vous vraiment supprimer cette classe ?")) return;
        try {
            await axios.delete(`http://localhost:8080/api/classes/${id}`);
            setClasses(prev => prev.filter(classe => classe.id !== id));
        } catch (error) {
            console.error("Erreur lors de la suppression :", error);
            alert("La suppression a échoué.");
        }
    };

    const handleEdit = (classe) => {
        setEditId(classe.id);
        setEditNom(classe.nom);
        setEditModule(classe.module);
        setEditAnneeScolaire(classe.anneeScolaire);
    };

    const handleSave = async (id) => {
        try {
            await axios.patch(`http://localhost:8080/api/classes/${id}`, {
                nom: editNom,
                module: editModule,
                anneeScolaire: editAnneeScolaire,
            });

            setClasses(prev =>
                prev.map(classe =>
                    classe.id === id ? { ...classe, nom: editNom, module: editModule, anneeScolaire: editAnneeScolaire } : classe
                )
            );
            setEditId(null);
        } catch (error) {
            console.error("Erreur lors de la modification :", error);
            alert("La modification a échoué.");
        }
    };

    const handleAddClass = async () => {
        try {
            const response = await axios.post("http://localhost:8080/api/classes", newClasse);
            setClasses(prev => [...prev, response.data]);
            setIsAdding(false);
            setNewClasse({
                nom: "",
                anneeScolaire: "",
                module: "",
                professeurId: localStorage.getItem("professeurId"),
                etudiants: []
            });
            alert("Classe ajoutée avec succès !");
        } catch (error) {
            console.error("Erreur lors de l'ajout :", error);
            alert("L'ajout de la classe a échoué.");
        }
    };

    const filteredClasses = classes.filter(classe =>
        classe.nom.toLowerCase().includes(search.toLowerCase()) ||
        classe.module.toLowerCase().includes(search.toLowerCase())
    );

    return (
        <Layout>
            <div className="flex flex-col h-screen">
                <div className="px-8 py-6">
                    <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 mb-6">
                        <div>
                            <h1 className="text-2xl font-semibold text-gray-800">Liste des classes</h1>
                            <p className="text-sm text-gray-500">Toutes les classes enregistrées</p>
                        </div>
                        <div className="flex flex-col sm:flex-row gap-2 sm:items-center">
                            <input
                                type="text"
                                placeholder="Rechercher par nom ou module..."
                                value={search}
                                onChange={(e) => setSearch(e.target.value)}
                                className="px-3 py-2 border rounded-md text-sm w-full sm:w-64 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                            <button
                                className="bg-black text-white px-4 py-2 rounded-md hover:bg-gray-800 transition"
                                onClick={() => setIsAdding(true)}
                            >
                                + Ajouter une classe
                            </button>
                        </div>
                    </div>
                </div>

                <div className="flex-1 overflow-hidden px-8 pb-8">
                    <div className="bg-white p-6 shadow-md rounded-xl border h-full overflow-auto">
                        <table className="min-w-full text-sm text-left">
                            <thead className="bg-gray-100 text-gray-700 font-medium uppercase text-xs sticky top-0">
                            <tr>
                                <th className="p-3">Nom</th>
                                <th className="p-3">Module</th>
                                <th className="p-3">Année scolaire</th>
                                <th className="p-3">Étudiants</th>
                                <th className="p-3">Actions</th>
                            </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-200">
                            {filteredClasses.map((classe) => (
                                <tr key={classe.id} className="hover:bg-gray-50">
                                    <td className="p-3 font-semibold text-gray-800">
                                        {editId === classe.id ? (
                                            <input
                                                value={editNom}
                                                onChange={(e) => setEditNom(e.target.value)}
                                                className="border rounded px-2 py-1 text-sm w-full"
                                            />
                                        ) : (
                                            classe.nom
                                        )}
                                    </td>
                                    <td className="p-3 text-gray-700">
                                        {editId === classe.id ? (
                                            <input
                                                value={editModule}
                                                onChange={(e) => setEditModule(e.target.value)}
                                                className="border rounded px-2 py-1 text-sm w-full"
                                            />
                                        ) : (
                                            classe.module
                                        )}
                                    </td>
                                    <td className="p-3 text-gray-700">
                                        {editId === classe.id ? (
                                            <input
                                                value={editAnneeScolaire}
                                                onChange={(e) => setEditAnneeScolaire(e.target.value)}
                                                className="border rounded px-2 py-1 text-sm w-full"
                                            />
                                        ) : (
                                            classe.anneeScolaire
                                        )}
                                    </td>
                                    <td className="p-3 flex items-center gap-1 text-gray-700">
                                        <Users2 className="w-4 h-4 text-gray-500"/>
                                        {classe.etudiants.length}
                                    </td>
                                    <td className="p-3 flex items-center gap-4">
                                        {editId === classe.id ? (
                                            <button title="Enregistrer" onClick={() => handleSave(classe.id)}>
                                                <Save className="w-5 h-5 text-green-600 hover:text-green-800 transition"/>
                                            </button>
                                        ) : (
                                            <button title="Modifier" onClick={() => handleEdit(classe)}>
                                                <Edit2 className="w-5 h-5 text-blue-600 hover:text-blue-800 transition"/>
                                            </button>
                                        )}
                                        <button title="Supprimer" onClick={() => handleDelete(classe.id)}>
                                            <Trash2 className="w-5 h-5 text-red-600 hover:text-red-800 transition"/>
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            {filteredClasses.length === 0 && (
                                <tr>
                                    <td colSpan="5" className="p-4 text-center text-gray-500">
                                        Aucune classe trouvée.
                                    </td>
                                </tr>
                            )}
                            </tbody>
                        </table>
                    </div>
                </div>

                {isAdding && (
                    <div className="fixed inset-0 bg-gray-500 bg-opacity-75 flex justify-center items-center z-50">
                        <div className="bg-white p-6 rounded-lg shadow-lg w-96">
                            <h3 className="text-xl font-bold mb-4">Ajouter une nouvelle classe</h3>
                            <div className="mb-4">
                                <input
                                    type="text"
                                    placeholder="Nom de la classe"
                                    className="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    value={newClasse.nom}
                                    onChange={(e) => setNewClasse({ ...newClasse, nom: e.target.value })}
                                />
                            </div>
                            <div className="mb-4">
                                <input
                                    type="text"
                                    placeholder="Module"
                                    className="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    value={newClasse.module}
                                    onChange={(e) => setNewClasse({ ...newClasse, module: e.target.value })}
                                />
                            </div>
                            <div className="mb-4">
                                <input
                                    type="text"
                                    placeholder="Année scolaire"
                                    className="w-full p-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    value={newClasse.anneeScolaire}
                                    onChange={(e) => setNewClasse({ ...newClasse, anneeScolaire: e.target.value })}
                                />
                            </div>
                            <div className="flex justify-between">
                                <button
                                    onClick={() => setIsAdding(false)}
                                    className="bg-gray-300 text-black px-4 py-2 rounded hover:bg-gray-400 transition"
                                >
                                    Annuler
                                </button>
                                <button
                                    onClick={handleAddClass}
                                    className="bg-black text-white px-4 py-2 rounded hover:bg-gray-800 transition"
                                >
                                    Ajouter
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </Layout>
    );
}

export default ListeClasses;