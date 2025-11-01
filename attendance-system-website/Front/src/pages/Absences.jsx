import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Layout from "../components/Layout";

const AbsencesPage = () => {
    const [absences, setAbsences] = useState([]);
    const [etudiants, setEtudiants] = useState({}); // Pour stocker les infos des étudiants
    const [classes, setClasses] = useState({});// Pour stocker les informations des classes
    const [selectedAbsence, setSelectedAbsence] = useState(null);
    const [formData, setFormData] = useState({
        dateAbsence: "",
        justifiee: false,
        commentaire: "",
    });
    const deleteAbsence = async (absenceId) => {
        try {
            await axios.delete(`http://localhost:8080/api/absences/${absenceId}`);
            alert("Absence supprimée avec succès !");
            // Optionnel : mettre à jour la liste localement ou recharger les données
            setAbsences(prev => prev.filter(abs => abs.id !== absenceId));
        } catch (error) {
            console.error("Erreur lors de la suppression :", error);
            alert("Échec de la suppression de l'absence.");
        }
    };
    const handleEdit = (absence) => {
        setSelectedAbsence(absence);  // Mettre l'absence à modifier dans l'état
        setFormData({
            dateAbsence: absence.dateAbsence,
            justifiee: absence.justifiee,
            commentaire: absence.commentaire || '',
        });
    };
    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const updatedAbsence = {
                ...formData,  // données de l'absence
                etudiantId: selectedAbsence.etudiantId,  // Conserver uniquement l'ID de l'étudiant
            };

            const response = await axios.put(`http://localhost:8080/api/absences/${selectedAbsence.id}`, updatedAbsence);

            // Mise à jour de l'état des absences avec la réponse
            setAbsences(absences.map(absence =>
                absence.id === selectedAbsence.id ? response.data : absence
            ));

            setSelectedAbsence(null);
            setMessage("Absence mise à jour avec succès !");
        } catch (error) {
            setMessage("Erreur lors de la mise à jour de l'absence.");
            console.error("Erreur lors de la modification de l'absence", error);
        }
    };


    // Récupération des absences, des étudiants et des classes
    useEffect(() => {
        const fetchData = async () => {
            try {
                const responseAbsences = await axios.get('http://localhost:8080/api/absences');
                setAbsences(responseAbsences.data);

                // Récupérer les informations des étudiants (par leurs IDs)
                const etudiantIds = responseAbsences.data.map(absence => absence.etudiantId);
                const uniqueEtudiantIds = [...new Set(etudiantIds)]; // Enlever les doublons d'ID

                // Récupérer les étudiants
                const responseEtudiants = await Promise.all(
                    uniqueEtudiantIds.map(id => axios.get(`http://localhost:8080/api/etudiants/${id}`))
                );

                // Mappage des étudiants par ID
                const etudiantsMap = responseEtudiants.reduce((acc, curr) => {
                    acc[curr.data.id] = curr.data;
                    return acc;
                }, {});
                setEtudiants(etudiantsMap);

                // Récupérer les informations des classes (par leurs IDs)
                const classeIds = responseEtudiants.map(etudiant => etudiant.data.classeId);
                const uniqueClasseIds = [...new Set(classeIds)];

                // Récupérer les classes
                const responseClasses = await Promise.all(
                    uniqueClasseIds.map(id => axios.get(`http://localhost:8080/api/classes/${id}`))
                );

                // Mappage des classes par ID
                const classesMap = responseClasses.reduce((acc, curr) => {
                    acc[curr.data.id] = curr.data;
                    return acc;
                }, {});
                setClasses(classesMap);

            } catch (error) {
                console.error("Erreur lors de la récupération des absences, des étudiants ou des classes", error);
            }
        };

        fetchData();
    }, []);


    return (
        <Layout>
            <div className="container mx-auto mt-6">
                <h1 className="text-2xl font-bold mb-4">Gestion des Absences</h1>
                <table className="min-w-full bg-white border border-gray-200 rounded-md">
                    <thead>
                    <tr className="bg-gray-100">
                        <th className="px-6 py-3 text-left text-sm font-medium text-gray-700">Date</th>
                        <th className="px-6 py-3 text-left text-sm font-medium text-gray-700">Étudiant</th>
                        <th className="px-6 py-3 text-left text-sm font-medium text-gray-700">Classe</th>
                        <th className="px-6 py-3 text-left text-sm font-medium text-gray-700">Statut</th>
                        <th className="px-6 py-3 text-left text-sm font-medium text-gray-700">Commentaire</th>
                        <th className="px-6 py-3 text-left text-sm font-medium text-gray-700">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {absences.length > 0 ? (
                        absences.map(absence => {
                            const etudiant = etudiants[absence.etudiantId]; // Récupérer l'étudiant par ID
                            const classe = etudiant ? classes[etudiant.classeId] : null; // Récupérer la classe de l'étudiant

                            return (
                                <tr key={absence.id} className="border-t border-gray-200">
                                    <td className="px-6 py-4 text-sm text-gray-700">{absence.dateAbsence}</td>
                                    <td className="px-6 py-4 text-sm text-gray-700">
                                        {etudiant ? `${etudiant.nom} ${etudiant.prenom}` : 'Inconnu'}
                                    </td>
                                    <td className="px-6 py-4 text-sm text-gray-700">
                                        {classe ? classe.nom : 'Inconnu'}
                                    </td>
                                    <td className="px-6 py-4 text-sm text-gray-700">
                                        {absence.justifiee ? "Justifié" : "Non justifié"}
                                    </td>
                                    <td className="px-6 py-4 text-sm text-gray-700">{absence.commentaire || 'Aucun'}</td>
                                    <td className="px-6 py-4 text-sm text-gray-700">
                                        <button
                                            onClick={() => deleteAbsence(absence.id)}
                                            className="text-red-500 hover:text-white border border-red-500 hover:bg-red-500 focus:outline-none focus:ring-2 focus:ring-red-600 rounded-md px-3 py-1 transition duration-200"
                                        >
                                            Supprimer
                                        </button>

                                        <button
                                            onClick={() => handleEdit(absence)}
                                            className="text-blue-500 hover:text-white border border-blue-500 hover:bg-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-600 rounded-md px-3 py-1 ml-2 transition duration-200"
                                        >
                                            Modifier
                                        </button>
                                    </td>
                                </tr>
                            );
                        })
                    ) : (
                        <tr>
                            <td colSpan="6" className="px-6 py-4 text-center text-sm text-gray-500">
                                Aucune absence enregistrée.
                            </td>
                        </tr>
                    )}
                    </tbody>
                </table>
            </div>
            {selectedAbsence && (
                <div className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-md w-96">
                        <h2 className="text-xl font-bold mb-4">Modifier l'absence</h2>
                        <form onSubmit={handleSubmit} className="space-y-4">
                            <input
                                type="date"
                                value={formData.dateAbsence}
                                onChange={(e) => setFormData({ ...formData, dateAbsence: e.target.value })}
                                className="w-full border border-gray-300 rounded px-3 py-2"
                            />
                            <label className="flex items-center space-x-2">
                                <input
                                    type="checkbox"
                                    checked={formData.justifiee}
                                    onChange={(e) => setFormData({ ...formData, justifiee: e.target.checked })}
                                />
                                <span>Justifiée</span>
                            </label>
                            <textarea
                                value={formData.commentaire}
                                onChange={(e) => setFormData({ ...formData, commentaire: e.target.value })}
                                placeholder="Commentaire"
                                className="w-full border border-gray-300 rounded px-3 py-2"
                            />
                            <div className="flex justify-between">
                                <button
                                    type="submit"
                                    className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
                                >
                                    Sauvegarder
                                </button>
                                <button
                                    type="button"
                                    onClick={() => setSelectedAbsence(null)}
                                    className="bg-gray-300 px-4 py-2 rounded hover:bg-gray-400"
                                >
                                    Annuler
                                </button>
                            </div>
                        </form>
                </div>
            )

        </div>
            )}

        </Layout>
    );
}

export default AbsencesPage;
